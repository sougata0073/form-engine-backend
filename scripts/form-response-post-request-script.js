const POST_URL = "http://localhost:9093/api/v1/forms/27f455e2-033e-4b30-8471-1e3f7aea6fee/response";

const NUMBER_OF_REQUESTS = 10000;
const CONCURRENCY = 1000;

const HEADERS = {
    "Content-Type": "application/json"
};

const QUESTION_IDS = {
    SHORT_ANSWER: "869929410508006848",
    PARAGRAPH: "869929416883348061",
    MULTIPLE_CHOICE: "869929639689734441",
    CHECKBOX: "869929695754994951",
    DROPDOWN: "869929748313818952",
    FILE_UPLOAD: "869929802072211855",
    LINEAR_SCALE: "869930150711150281",
    RATING: "869930201432870845",
    MULTIPLE_CHOICE_GRID: "869930246932678098",
    TICK_BOX_GRID: "869930349697322494",
    DATE: "869930437031120815",
    TIME: "869931163455209813",
    DATE_TIME: "869931193138302377",
    DURATION: "869931232669615461"
};

const MULTIPLE_CHOICE_OPTIONS = [
    "869929655368044251",
    "869929693154528894",
    "869929693158721625",
    "869929693158721626",
    "869929693158721627"
];

const CHECKBOX_OPTIONS = [
    "869929710816742065",
    "869929744102738962",
    "869929744102738963",
    "869929744102738964"
];

const DROPDOWN_OPTIONS = [
    "869929763761442054",
    "869929800482570683",
    "869929800482570684",
    "869929800482570685",
    "869929800482570686"
];

const MCG_ROWS = [
    "869930264481644805",
    "869930349672153797",
    "869930349672153798",
    "869930349672153799",
    "869930349672153800"
];

const MCG_COLUMNS = [
    "869930264481644806",
    "869930349688934298",
    "869930349688934299",
    "869930349688934300",
    "869930349688934301",
    "869930349688934302",
    "869930349688934303",
    "869930349688934304",
    "869930349688934305",
    "869930349688934306"
];

const TBG_ROWS = [
    "869930369020477564",
    "869930434359345294",
    "869930434359345295",
    "869930434359345296",
    "869930434359345297"
];

const TBG_COLUMNS = [
    "869930369020477565",
    "869930434388708634",
    "869930434388708635",
    "869930434388708636",
    "869930434388708637",
    "869930434388708638",
    "869930434388708639",
    "869930434388708640",
    "869930434388708641",
    "869930434388708642"
];

function randomItem(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

function randomSubset(arr) {
    const shuffled = [...arr].sort(() => Math.random() - 0.5);
    const count = Math.floor(Math.random() * arr.length) + 1;
    return shuffled.slice(0, count);
}

function randomDate() {
    const start = new Date("2000-01-01");
    const end = new Date("2030-12-31");

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
                rating: Math.floor(Math.random() * 10) + 1,
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
                hours: Math.floor(Math.random() * 73),
                minutes: Math.floor(Math.random() * 60),
                seconds: Math.floor(Math.random() * 60),
                questionId: QUESTION_IDS.DURATION,
                questionType: "DURATION"
            }

        ]
    };
}

async function sendRequests() {

    console.time("⏱ Total Time");

    const failedRequests = [];
    let successCount = 0;

    for (let start = 1; start <= NUMBER_OF_REQUESTS; start += CONCURRENCY) {

        const promises = [];

        for (
            let i = start;
            i < Math.min(start + CONCURRENCY, NUMBER_OF_REQUESTS + 1);
            i++
        ) {

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
                            process.stdout?.write?.("✅");
                            return;
                        }

                        let message = "";

                        try {
                            message = await response.text();
                        } catch {
                            message = "Unknown error";
                        }

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

                        process.stdout?.write?.("❌");
                    })
                    .catch(error => {

                        failedRequests.push({
                            request: i,
                            status: "NETWORK",
                            reason: error.message
                        });

                        process.stdout?.write?.("❌");
                    })
            );
        }

        await Promise.all(promises);

        process.stdout?.write?.(
            `  [${Math.min(start + CONCURRENCY - 1, NUMBER_OF_REQUESTS)}/${NUMBER_OF_REQUESTS}]\n`
        );
    }

    console.timeEnd("⏱ Total Time");

    console.log("\n");
    console.log("══════════════════════════════════════════════");
    console.log("LOAD TEST SUMMARY");
    console.log("══════════════════════════════════════════════");
    console.log(`Total Requests : ${NUMBER_OF_REQUESTS}`);
    console.log(`Successful     : ${successCount}`);
    console.log(`Failed         : ${failedRequests.length}`);
    console.log(`Success Rate   : ${(successCount / NUMBER_OF_REQUESTS * 100).toFixed(2)}%`);
    console.log("══════════════════════════════════════════════");

    if (failedRequests.length === 0) {
        console.log("\nAll requests completed successfully.");
        return;
    }

    let grouped = {};

    for (const f of failedRequests) {
        const key = `${f.status} | ${f.reason}`;
        grouped[key] = (grouped[key] || 0) + 1;
    }

    console.log("\nFAILED REQUESTS");
    console.log("══════════════════════════════════════════════");

    Object.entries(grouped)
        .sort((a, b) => b[1] - a[1])
        .forEach(([message, count]) => {
            console.log(`${count}x  ${message}`);
        });

    console.log("══════════════════════════════════════════════");

    grouped = {};

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