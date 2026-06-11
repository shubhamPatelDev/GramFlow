const admin = require("firebase-admin");
const serviceAccount = require("../backend/src/main/resources/firebase-service-account.json");

if (!admin.apps.length) {
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount)
    });
}

const db = admin.firestore();

async function deleteIG() {
    const usersSnapshot = await db.collection("users").where("email", "==", "patel463shubham@gmail.com").get();
    if (usersSnapshot.empty) {
        console.log("User not found");
        return;
    }
    const userId = usersSnapshot.docs[0].id;
    console.log("User ID:", userId);
    
    // Correct collection name: instagram_accounts
    const igSnapshot = await db.collection("instagram_accounts").where("userId", "==", userId).get();
    if (igSnapshot.empty) {
        console.log("No IG account found for user in instagram_accounts");
        return;
    }
    
    for (const doc of igSnapshot.docs) {
        await doc.ref.delete();
        console.log("Deleted IG account document:", doc.id);
    }
}

deleteIG();
