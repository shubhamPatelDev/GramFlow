const admin = require("firebase-admin");
const serviceAccount = require("../backend/src/main/resources/firebase-service-account.json");

if (!admin.apps.length) {
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount)
    });
}

const db = admin.firestore();

async function check() {
    const docs = await db.collection("instagram_accounts").get();
    if (docs.empty) {
        console.log("0 accounts found in database.");
        return;
    }
    console.log(`Found ${docs.size} accounts in database:`);
    docs.forEach(doc => {
        console.log("Document ID:", doc.id);
        console.log("Data:", doc.data());
    });
}

check();
