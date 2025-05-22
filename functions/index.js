const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.notifyNewShift = functions.database
    .onValueCreated({
      ref: "/shifts/{shiftId}",
      region: "europe-west1",
    }, (snapshot, context) => {
      const shiftData = snapshot.data._data;
      const payload = {
        notification: {
          title: "New Shift Available",
          body: `${shiftData.healthAideName.fullName}
          you have a new shift starts at ${shiftData.shiftDate}`,
        },
        topic: `${shiftData.healthAideName.id}`,
      };

      return admin.messaging().send(payload);
    },
    );

exports.notifyNewShiftAssignment = functions.database
    .onValueUpdated(
        {
          ref: "/shifts/{shiftId}",
          region: "europe-west1",
        },
        (event) => {
          const before = event && event.data && event.data.before;
          const after = event && event.data && event.data.after;

          const beforeJson = before._data;
          const afterJson = after._data;
          const beforeCarer = beforeJson.healthAideName;
          const afterCarer = afterJson.healthAideName;
          const oldCarerId = beforeCarer.id;
          const newCarerId = afterCarer.id;

          // If the assigned carer has changed
          if (oldCarerId && newCarerId && oldCarerId !== newCarerId) {
            const messages = [];

            // Notify new carer
            messages.push(
                admin.messaging().send({
                  notification: {
                    title: "New Shift Assigned",
                    body: `${afterCarer.fullName}, you have been assigned a new shift on ${afterJson.shiftDate}.`,
                  },
                  topic: `${newCarerId}`,
                }),
            );

            // Notify removed carer
            messages.push(
                admin.messaging().send({
                  notification: {
                    title: "Shift Removed",
                    body: `${beforeCarer.fullName} unfortunately you've been removed from your shift on ${beforeJson.shiftDate}.`,
                  },
                  topic: `${oldCarerId}`,
                }),
            );

            return Promise.all(messages);
          }

          return null;
        },
    );


exports.notifyNewVitalsLog = functions.database
    .onValueCreated({
      ref: "/vitals/{vitalsId}",
      region: "europe-west1",
    }, (snapshot, context) => {
      const vitalsData = snapshot.data._data;
      const payload = {
        notification: {
          title: "New Log Today",
          body: `New vitals log created by ${vitalsData.loggerId}`,
        },
        topic: "vitals",
      };

      return admin.messaging().send(payload);
    },
    );
