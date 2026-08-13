const POST_URL = "http://localhost:9093/api/v1/forms/ea620ff5-d0b0-4484-ac36-64d5b951ea97/response";

const NUMBER_OF_REQUESTS = 1;
const CONCURRENCY = 2000;

const HEADERS = {
    "Content-Type": "application/json"
};

const QUESTION_IDS = {
    SHORT_ANSWER: "873605699895328670",
    PARAGRAPH: "873605850999324236",
    MULTIPLE_CHOICE: "873605898768252079",
    CHECKBOX: "873606063986078173",
    DROPDOWN: "873606157909130597",
    FILE_UPLOAD: "873606250137680915",
    LINEAR_SCALE: "873606900376433115",
    RATING: "873606950221543862",
    MULTIPLE_CHOICE_GRID: "873607021621180685",
    TICK_BOX_GRID: "873607100956439451",
    DATE: "873607201879784749",
    TIME: "873607261132717283",
    DATE_TIME: "873607289918223804",
    DURATION: "873607325553031003"
};

const MULTIPLE_CHOICE_OPTIONS = [
    "873605912169053244",
    "873605993584687615",
    "873605993584687616",
    "873605993584687617",
    "873606174023643172"
];

const CHECKBOX_OPTIONS = [
    "873606080176091666",
    "873606158001403355",
    "873606158001403356",
    "873606158001403357"
];

const DROPDOWN_OPTIONS = [
    "873606185633480059",
    "873606249504340607",
    "873606249504340608",
    "873606249504340609"
];

const MCG_ROWS = [
    "873607041175026588",
    "873607099656206364",
    "873607099656206365",
    "873607099656206366",
    "873607099656206367"
];

const MCG_COLUMNS = [
    "873607041175026589",
    "873607099928837505",
    "873607099928837506",
    "873607099928837507",
    "873607099928837508",
    "873607099928837509",
    "873607099928837510",
    "873607099928837511",
    "873607099928837512",
    "873607099928837513"
];

const TBG_ROWS = [
    "873607122326418760",
    "873607189913435657",
    "873607189913435658",
    "873607189913435659",
    "873607189913435660",
    "873607189913435661"
];

const TBG_COLUMNS = [
    "873607122326418761",
    "873607190215424938",
    "873607190215424939",
    "873607190215424940",
    "873607190215424941",
    "873607190215424942",
    "873607190215424943",
    "873607190215424944",
    "873607190215424945",
    "873607190215424946"
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
                fileName: `image_${index}.jpg`,
                fileUrl: `https://picsum.photos/seed/${index}/1200/800`,
                fileSize: Math.floor(Math.random() * (10 * 1024 * 1024 - 100 * 1024 + 1)) + 100 * 1024,
                fileMimeType: "image/jpeg",
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

                        let message;

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