package com.example.criptotienda.network

import com.example.criptotienda.model.Crypto
import com.example.criptotienda.model.User
import com.google.firebase.firestore.FirebaseFirestore

const val CRYPTO_COLLECTION = "cryptos"
const val USERS_COLLECTION = "users"
const val CRYPTOLIST_COLLECTION = "cryptolist"

class FirestoreService( val firebaseFirestore: FirebaseFirestore ) {

    fun setDocument(data: Any, collectionName: String, id: String, callback: Callback<Void>) {
        firebaseFirestore.collection(collectionName).document(id).set(data)
            .addOnSuccessListener { callback.onSuccess(null) }
            .addOnFailureListener{ exception -> callback.onFailed(exception)}
    }

    fun updateUser(user: User, callback: Callback<User>?) {
        firebaseFirestore.collection(USERS_COLLECTION).document(user.username)
            .update(CRYPTOLIST_COLLECTION, user.cryptolist)
            .addOnSuccessListener { result ->
                if( callback!=null )
                    callback.onSuccess(user)
            }
            .addOnFailureListener {
                exception ->
                if (callback != null) {
                    callback.onFailed(exception)
                }
            }
    }

    fun updateCrypto(crypto: Crypto) {
        firebaseFirestore.collection(CRYPTO_COLLECTION).document(crypto.getDocumentId())
            .update("available", crypto.available)
    }

    fun getCryptos(callback: Callback<List<Crypto>>?) {
        firebaseFirestore.collection(CRYPTO_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    val cryptoList = result.toObjects(Crypto::class.java)
                    if (callback != null) {
                        callback.onSuccess(cryptoList)
                    }
                    break
                }
            }
            .addOnFailureListener { exception ->
                if (callback != null) {
                    callback.onFailed(exception)
                }
            }

    }

    fun findUserById(id: String, callback: Callback<User>) {
        firebaseFirestore.collection(USERS_COLLECTION).document(id)
            .get()
            .addOnSuccessListener {
                    result ->
                if( result.data != null ) {
                    callback.onSuccess(result.toObject(User::class.java))
                } else {
                    callback.onSuccess(null)
                }
            }
            .addOnFailureListener{ exception ->
                callback.onFailed(exception)
            }

    }

    fun listenForUpdates(cryptos: List<Crypto>, listener: RealtimeDataListener<Crypto>) {
        val cryptoReference = firebaseFirestore.collection(CRYPTO_COLLECTION)
        for(crypto in cryptos) {
            cryptoReference.document(crypto.getDocumentId()).addSnapshotListener {
                snapshot, e ->
                if( e!=null ) {
                    listener.onError(e)
                }
                if(snapshot!=null && snapshot.exists()) {
                    listener.onDataChange(snapshot.toObject(Crypto::class.java)!!)
                }
            }
        }
    }

    fun listenForUpdates(user: User, listener: RealtimeDataListener<User>) {
        val userReference = firebaseFirestore.collection(USERS_COLLECTION)
        userReference.document(user.username).addSnapshotListener {
            snapshot, e ->
            if( e!=null ) {
                listener.onError(e)
            }
            if(snapshot!=null && snapshot.exists()) {
                listener.onDataChange(snapshot.toObject(User::class.java)!!)
            }
        }
    }

}