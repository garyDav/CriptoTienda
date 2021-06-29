Crear proyecto en firestore: https://console.firebase.google.com/
	- En authentication habilitar el login anonimo
	

Establecer las reglas en firestore:

	rules_version = '2';
	service cloud.firestore {
	  match /databases/{database}/documents {
		match /users/{userId} {
		  // Permite modificar la información de los usuarios si el userId del modificador es el mismo del usuario a modificar
		  allow read, update, delete: if request.auth.uid == userID;
		  // Permite crear usuarios si estamos autenticados porque, si el usuario no existe, no podemos verificar los IDs
		  allow create: if request.auth.uid != null;
		}
	  }
	}