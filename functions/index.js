/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// const {onRequest} = require("firebase-functions/v2/https");
// const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.notifyNewShift = functions.database
    .onValueCreated({
      ref: "/shifts/{shiftId}",
      region: "europe-west1",
    }, (snapshot, context) => {
      const shiftData = snapshot.val();

      const payload = {
        notification: {
          title: "New Shift Available",
          body: `${shiftData.title} starts at ${shiftData.time}`,
        },
        topic: "shifts",
      };

      return admin.messaging().send(payload);
    },
    );

