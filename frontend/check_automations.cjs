const admin = require("firebase-admin");
const serviceAccount = require("../backend/src/main/resources/firebase-service-account.json");

if (!admin.apps.length) {
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount)
    });
}

const db = admin.firestore();

async function check() {
    const automations = await db.collection("automations").get();
    if (automations.empty) {
        console.log("0 automations found in database.");
        return;
    }
    console.log(`Found ${automations.size} automations in database:`);
    automations.forEach(doc => {
        console.log(doc.id, "=>", doc.data());
    });
}

check();
