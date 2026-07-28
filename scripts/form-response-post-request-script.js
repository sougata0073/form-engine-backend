/**
 * ===========================================================
 * CONFIGURATION
 * ===========================================================
 */

const POST_URL = "http://localhost:9093/api/v1/forms/d61a0453-b973-41ae-ba5a-5a9101881e4a/response";

const NUMBER_OF_REQUESTS = 1;

const HEADERS = {
    "Content-Type": "application/json"
};

// ==============================
// QUESTION IDS
// ==============================

const QUESTION_IDS = {
    SHORT_ANSWER: "868768371739520614",
    PARAGRAPH: "868768380446893752",
    MULTIPLE_CHOICE: "868770752229335063",
    CHECKBOX: "868770814078543252",
    DROPDOWN: "868770854977203232",
    FILE_UPLOAD: "868770892008710523",
    LINEAR_SCALE: "868771499809498277",
    RATING: "868771534903242204",
    MULTIPLE_CHOICE_GRID: "868771565605546271",
    TICK_BOX_GRID: "868771681603218544",
    DATE: "868771746828837672",
    TIME: "868771770803482368",
    DATE_TIME: "868771801736473306",
    DURATION: "868777278398588701"
};

// ==============================
// OPTIONS
// ==============================

const MULTIPLE_CHOICE_OPTIONS = [
    "868770765625943489",
    "868770801596295847",
    "868770801596295848",
    "868770801596295849",
    "868770801596295850"
];

const CHECKBOX_OPTIONS = [
    "868770828330789340",
    "868770852640974515",
    "868770852640974516",
    "868770852640974517"
];

const DROPDOWN_OPTIONS = [
    "868770868709354707",
    "868770891794801796",
    "868770891794801797",
    "868770891794801798"
];

// ==============================
// MULTIPLE CHOICE GRID
// ==============================

const MCG_ROWS = [
    "868771594521078540",
    "868771681510942640",
    "868771681510942641",
    "868771681510942642",
    "868771681510942643"
];

const MCG_COLUMNS = [
    "868771594521078541",
    "868771681510942645",
    "868771681510942646",
    "868771681510942647",
    "868771681510942648",
    "868771681510942649",
    "868771681510942650",
    "868771681510942651",
    "868771681510942652",
    "868771681510942653"
];

// ==============================
// TICK BOX GRID
// ==============================

const TBG_ROWS = [
    "868771696476217885",
    "868771747877415121",
    "868771747877415122",
    "868771747877415123",
    "868771747877415124"
];

const TBG_COLUMNS = [
    "868771696476217886",
    "868771747881609457",
    "868771747881609458",
    "868771747881609459",
    "868771747881609460",
    "868771747881609461",
    "868771747881609462",
    "868771747881609463",
    "868771747881609464",
    "868771747881609465"
];

/**
 * ===========================================================
 * HELPERS
 * ===========================================================
 */

function randomItem(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

function randomSubset(arr) {
    const shuffled = [...arr].sort(() => Math.random() - 0.5);
    const count = Math.floor(Math.random() * arr.length) + 1;
    return shuffled.slice(0, count);
}

function randomDate() {
    const start = new Date("2026-01-01");
    const end = new Date("2026-12-31");

    return new Date(
        start.getTime() +
        Math.random() * (end.getTime() - start.getTime())
    );
}

function isoDate(date) {
    return new Date(
        Date.UTC(
            date.getUTCFullYear(),
            date.getUTCMonth(),
            date.getUTCDate(),
            18,
            30,
            0
        )
    ).toISOString();
}

function isoTime() {
    const d = new Date();

    d.setUTCHours(
        Math.floor(Math.random() * 24),
        Math.floor(Math.random() * 60),
        0,
        0
    );

    return d.toISOString();
}

function uuid() {
    return crypto.randomUUID();
}

/**
 * ===========================================================
 * GENERATE ONE RESPONSE
 * ===========================================================
 */

function generatePayload(index) {

    const date = randomDate();

    return {
        responses: [

            {
                text: `Short answer ${index}`,
                questionId: QUESTION_IDS.SHORT_ANSWER,
                questionType: "SHORT_ANSWER"
            },

            {
                text: `Paragraph response ${index}.`,
                questionId: QUESTION_IDS.PARAGRAPH,
                questionType: "PARAGRAPH"
            },

            {
                responseOptionId: randomItem(MULTIPLE_CHOICE_OPTIONS),
                questionId: QUESTION_IDS.MULTIPLE_CHOICE,
                questionType: "MULTIPLE_CHOICE"
            },

            {
                responseOptionIds: randomSubset(CHECKBOX_OPTIONS),
                questionId: QUESTION_IDS.CHECKBOX,
                questionType: "CHECKBOX"
            },

            {
                responseOptionId: randomItem(DROPDOWN_OPTIONS),
                questionId: QUESTION_IDS.DROPDOWN,
                questionType: "DROPDOWN"
            },

            {
                fileName: `image_${index}.png`,
                fileUrl: `https://example.com/image_${index}.png`,
                fileSize: Math.floor(Math.random() * 5000000) + 100000,
                fileMimeType: "image/png",
                questionId: QUESTION_IDS.FILE_UPLOAD,
                questionType: "FILE_UPLOAD"
            },

            {
                scale: Math.floor(Math.random() * 5) + 1,
                questionId: QUESTION_IDS.LINEAR_SCALE,
                questionType: "LINEAR_SCALE"
            },

            {
                rating: Math.floor(Math.random() * 5) + 1,
                questionId: QUESTION_IDS.RATING,
                questionType: "RATING"
            },

            {
                rows: MCG_ROWS.map(rowId => ({
                    rowId,
                    responseColumnId: randomItem(MCG_COLUMNS)
                })),
                questionId: QUESTION_IDS.MULTIPLE_CHOICE_GRID,
                questionType: "MULTIPLE_CHOICE_GRID"
            },

            {
                rows: TBG_ROWS.map(rowId => ({
                    rowId,
                    responseColumnIds: randomSubset(TBG_COLUMNS)
                })),
                questionId: QUESTION_IDS.TICK_BOX_GRID,
                questionType: "TICK_BOX_GRID"
            },

            {
                date: isoDate(date),
                questionId: QUESTION_IDS.DATE,
                questionType: "DATE"
            },

            {
                time: isoTime(),
                questionId: QUESTION_IDS.TIME,
                questionType: "TIME"
            },

            {
                dateTime: date.toISOString(),
                questionId: QUESTION_IDS.DATE_TIME,
                questionType: "DATE_TIME"
            },

            {
                hours: Math.floor(Math.random() * 24),
                minutes: Math.floor(Math.random() * 60),
                seconds: Math.floor(Math.random() * 60),
                questionId: QUESTION_IDS.DURATION,
                questionType: "DURATION"
            }

        ]
    };
}

/**
 * ===========================================================
 * SEND REQUESTS
 * ===========================================================
 */

async function sendRequests() {

    console.time("⏱ Total Time");

    const failedRequests = [];
    let successCount = 0;

    const promises = [];

    for (let i = 1; i <= NUMBER_OF_REQUESTS; i++) {

        const payload = generatePayload(i);

        promises.push(

            fetch(POST_URL, {
                method: "POST",
                headers: {
                    ...HEADERS,
                    "auth-jwt": uuid()
                },
                body: JSON.stringify(payload)
            })
            .then(async response => {

                if (response.ok) {
                    successCount++;
                    process.stdout?.write?.("✓");
                    return;
                }

                let message = "";

                try {
                    message = await response.text();
                } catch {
                    message = "Unknown error";
                }

                // Keep only first line and first 120 chars
                message = message
                    .replace(/\n/g, " ")
                    .replace(/\s+/g, " ")
                    .trim()
                    .substring(0, 120);

                failedRequests.push({
                    request: i,
                    status: response.status,
                    reason: message || response.statusText
                });

                process.stdout?.write?.("✗");
            })
            .catch(error => {

                failedRequests.push({
                    request: i,
                    status: "NETWORK",
                    reason: error.message
                });

                process.stdout?.write?.("✗");
            })

        );
    }

    await Promise.all(promises);

    console.timeEnd("⏱ Total Time");

    console.log("\n");
    console.log("══════════════════════════════════════════════");
    console.log("           LOAD TEST SUMMARY");
    console.log("══════════════════════════════════════════════");
    console.log(`📦 Total Requests : ${NUMBER_OF_REQUESTS}`);
    console.log(`✅ Successful     : ${successCount}`);
    console.log(`❌ Failed         : ${failedRequests.length}`);
    console.log(`📈 Success Rate   : ${(successCount / NUMBER_OF_REQUESTS * 100).toFixed(2)}%`);
    console.log("══════════════════════════════════════════════");

    if (failedRequests.length === 0) {
        console.log("\n🎉 All requests completed successfully.");
        return;
    }

    console.log("\n❌ FAILED REQUESTS");
    console.log("──────────────────────────────────────────────────────────────────────────────");

    failedRequests.forEach(f => {
        console.log(
            `#${String(f.request).padStart(4)}  |  ${String(f.status).padEnd(8)}  |  ${f.reason}`
        );
    });

    console.log("──────────────────────────────────────────────────────────────────────────────");

    // Failure summary
    const grouped = {};

    for (const f of failedRequests) {
        const key = `${f.status}`;
        grouped[key] = (grouped[key] || 0) + 1;
    }

    console.log("\n📊 FAILURE BREAKDOWN");

    Object.entries(grouped)
        .sort((a, b) => b[1] - a[1])
        .forEach(([status, count]) => {
            console.log(`   ${status} : ${count}`);
        });

    console.log();
}

sendRequests()