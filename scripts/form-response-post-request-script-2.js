// ============================================================
// CONFIGURATION
// ============================================================

const FORM_IDS = [
    "d962ea10-f3c8-4d69-81b0-6aaf8d35f736"
];

const VIEW_URL = "http://localhost:9092/api/v1/forms";
const RESPONSE_URL = "http://localhost:9093/api/v1/forms";

const NUMBER_OF_REQUESTS = 100;
const CONCURRENCY = 2000;

const HEADERS = {
    "Content-Type": "application/json"
};


// ============================================================
// RANDOM HELPERS
// ============================================================

function randomItem(arr) {
    if (!arr || arr.length === 0) {
        return null;
    }

    return arr[Math.floor(Math.random() * arr.length)];
}


function randomSubset(arr) {
    if (!arr || arr.length === 0) {
        return [];
    }

    const shuffled = [...arr].sort(() => Math.random() - 0.5);

    const count =
        Math.floor(Math.random() * arr.length) + 1;

    return shuffled.slice(0, count);
}


function randomDate() {

    const start = new Date("2000-01-01");
    const end = new Date("2030-12-31");

    return new Date(
        start.getTime() +
        Math.random() *
        (end.getTime() - start.getTime())
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


// ============================================================
// FETCH FORM SCHEMA
// ============================================================

async function fetchFormSchema(formId, userId) {

    const url =
        `${VIEW_URL}/${formId}/view`;

    const response = await fetch(url, {

        method: "GET",

        headers: {
            ...HEADERS,
            "auth-jwt": userId
        }
    });


    if (!response.ok) {

        const message =
            await response.text();

        throw new Error(
            `Failed to fetch form ${formId}. ` +
            `Status: ${response.status}. ` +
            `Response: ${message}`
        );
    }


    const form =
        await response.json();


    if (
        !form.questions ||
        !Array.isArray(form.questions)
    ) {

        throw new Error(
            `Invalid form response for ${formId}: ` +
            `questions array not found`
        );
    }


    return form;
}


// ============================================================
// PAYLOAD GENERATOR
// ============================================================

function generatePayload(form, index) {

    const date = randomDate();

    const responses = [];


    for (const question of form.questions) {

        const questionId =
            question.id;

        const questionType =
            question.questionType;

        let response;


        // ----------------------------------------------------
        // SHORT ANSWER
        // ----------------------------------------------------

        if (
            questionType ===
            "SHORT_ANSWER"
        ) {

            response = {

                text:
                    `Short answer ${index}`,

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // PARAGRAPH
        // ----------------------------------------------------

        else if (
            questionType ===
            "PARAGRAPH"
        ) {

            response = {

                text:
                    `Paragraph response ${index}.`,

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // MULTIPLE CHOICE
        // ----------------------------------------------------

        else if (
            questionType ===
            "MULTIPLE_CHOICE"
        ) {

            response = {

                responseOptionId:
                    randomItem(
                        (question.options || [])
                            .map(option => option.id)
                    ),

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // CHECKBOX
        // ----------------------------------------------------

        else if (
            questionType ===
            "CHECKBOX"
        ) {

            response = {

                responseOptionIds:
                    randomSubset(
                        (question.options || [])
                            .map(option => option.id)
                    ),

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // DROPDOWN
        // ----------------------------------------------------

        else if (
            questionType ===
            "DROPDOWN"
        ) {

            response = {

                responseOptionId:
                    randomItem(
                        (question.options || [])
                            .map(option => option.id)
                    ),

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // FILE UPLOAD
        // ----------------------------------------------------

        else if (
            questionType ===
            "FILE_UPLOAD"
        ) {

            response = {

                fileName:
                    `image_${index}.jpg`,

                fileUrl:
                    `https://picsum.photos/seed/${index}/1200/800`,

                fileSize:
                    Math.floor(
                        Math.random() *
                        (
                            10 * 1024 * 1024 -
                            100 * 1024 +
                            1
                        )
                    ) +
                    100 * 1024,

                fileMimeType:
                    "image/jpeg",

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // LINEAR SCALE
        // ----------------------------------------------------

        else if (
            questionType ===
            "LINEAR_SCALE"
        ) {

            const from =
                question.fromNumber ?? 1;

            const to =
                question.toNumber ?? 5;


            response = {

                scale:
                    Math.floor(
                        Math.random() *
                        (to - from + 1)
                    ) +
                    from,

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // RATING
        // ----------------------------------------------------

        else if (
            questionType ===
            "RATING"
        ) {

            const maxRating =
                question.maxRatingNumber ?? 10;


            response = {

                rating:
                    Math.floor(
                        Math.random() *
                        maxRating
                    ) +
                    1,

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // MULTIPLE CHOICE GRID
        // ----------------------------------------------------

        else if (
            questionType ===
            "MULTIPLE_CHOICE_GRID"
        ) {

            const columns =
                question.columns || [];

            const rows =
                question.rows || [];


            response = {

                rows:
                    rows.map(row => ({

                        rowId:
                            row.id,

                        responseColumnId:
                            randomItem(
                                columns.map(
                                    column => column.id
                                )
                            )
                    })),

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // TICK BOX GRID
        // ----------------------------------------------------

        else if (
            questionType ===
            "TICK_BOX_GRID"
        ) {

            const columns =
                question.columns || [];

            const rows =
                question.rows || [];


            response = {

                rows:
                    rows.map(row => ({

                        rowId:
                            row.id,

                        responseColumnIds:
                            randomSubset(
                                columns.map(
                                    column => column.id
                                )
                            )
                    })),

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // DATE
        // ----------------------------------------------------

        else if (
            questionType ===
            "DATE"
        ) {

            response = {

                date:
                    isoDate(date),

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // TIME
        // ----------------------------------------------------

        else if (
            questionType ===
            "TIME"
        ) {

            response = {

                time:
                    isoTime(),

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // DATE TIME
        // ----------------------------------------------------

        else if (
            questionType ===
            "DATE_TIME"
        ) {

            response = {

                dateTime:
                    date.toISOString(),

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // DURATION
        // ----------------------------------------------------

        else if (
            questionType ===
            "DURATION"
        ) {

            response = {

                hours:
                    Math.floor(
                        Math.random() * 73
                    ),

                minutes:
                    Math.floor(
                        Math.random() * 60
                    ),

                seconds:
                    Math.floor(
                        Math.random() * 60
                    ),

                questionId,
                questionType
            };
        }


        // ----------------------------------------------------
        // UNKNOWN QUESTION TYPE
        // ----------------------------------------------------

        else {

            console.warn(
                `Unknown question type "${questionType}" ` +
                `for question ${questionId}. Skipping.`
            );

            continue;
        }


        responses.push(response);
    }


    return {
        responses
    };
}


// ============================================================
// SUBMIT ONE RESPONSE
//
// ONE GROUP =
//     1 GET schema
//     1 POST response
//
// Both requests use the SAME userId.
// ============================================================

async function submitOneResponse(
    formId,
    requestIndex
) {

    // --------------------------------------------------------
    // Generate a UNIQUE user ID for this GET + POST pair
    // --------------------------------------------------------

    const userId = uuid();


    try {

        // ====================================================
        // 1. GET FORM SCHEMA
        // ====================================================

        const form =
            await fetchFormSchema(
                formId,
                userId
            );


        // ====================================================
        // 2. GENERATE PAYLOAD
        // ====================================================

        const payload =
            generatePayload(
                form,
                requestIndex
            );


        // ====================================================
        // 3. POST RESPONSE
        // ====================================================

        const postUrl =
            `${RESPONSE_URL}/${formId}/response`;


        const response =
            await fetch(
                postUrl,
                {

                    method: "POST",

                    headers: {
                        ...HEADERS,

                        // SAME userId as GET
                        "auth-jwt": userId
                    },

                    body:
                        JSON.stringify(
                            payload
                        )
                }
            );


        if (!response.ok) {

            let message;

            try {
                message =
                    await response.text();
            } catch {
                message =
                    "Unknown error";
            }


            message =
                message
                    .replace(/\n/g, " ")
                    .replace(/\s+/g, " ")
                    .trim()
                    .substring(0, 120);


            return {

                success: false,

                request:
                    requestIndex,

                status:
                    response.status,

                reason:
                    message ||
                    response.statusText
            };
        }


        return {

            success: true,

            request:
                requestIndex
        };

    } catch (error) {

        return {

            success: false,

            request:
                requestIndex,

            status:
                "NETWORK",

            reason:
                error.message
        };
    }
}


// ============================================================
// SEND REQUESTS FOR ONE FORM
// ============================================================

async function sendRequestsForForm(formId) {

    console.log("\n");
    console.log(
        "================================================"
    );

    console.log(
        `FORM ID: ${formId}`
    );

    console.log(
        `REQUEST GROUPS: ${NUMBER_OF_REQUESTS}`
    );

    console.log(
        `CONCURRENCY: ${CONCURRENCY}`
    );

    console.log(
        "================================================"
    );


    console.time(
        `Total Time - ${formId}`
    );


    const failedRequests = [];

    let successCount = 0;


    // ========================================================
    // PROCESS GROUPS IN BATCHES
    // ========================================================

    for (
        let start = 1;

        start <= NUMBER_OF_REQUESTS;

        start += CONCURRENCY
    ) {

        const promises = [];


        // ----------------------------------------------------
        // Create CONCURRENCY number of GET + POST groups
        // ----------------------------------------------------

        for (

            let i = start;

            i <
            Math.min(
                start + CONCURRENCY,
                NUMBER_OF_REQUESTS + 1
            );

            i++
        ) {

            promises.push(
                submitOneResponse(
                    formId,
                    i
                )
            );
        }


        // ----------------------------------------------------
        // Run all groups simultaneously
        // ----------------------------------------------------

        const results =
            await Promise.all(
                promises
            );


        // ----------------------------------------------------
        // Process results
        // ----------------------------------------------------

        for (const result of results) {

            if (result.success) {

                successCount++;

                process.stdout
                    ?.write?.("✅");

            } else {

                failedRequests.push(
                    result
                );

                process.stdout
                    ?.write?.("❌");
            }
        }


        process.stdout?.write?.(
            `  [` +
            `${Math.min(
                start + CONCURRENCY - 1,
                NUMBER_OF_REQUESTS
            )}` +
            `/${NUMBER_OF_REQUESTS}]\n`
        );
    }


    console.timeEnd(
        `Total Time - ${formId}`
    );


    // ========================================================
    // SUMMARY
    // ========================================================

    console.log("\n");

    console.log(
        "══════════════════════════════════════════════"
    );

    console.log(
        `LOAD TEST SUMMARY - ${formId}`
    );

    console.log(
        "══════════════════════════════════════════════"
    );

    console.log(
        `Total Requests : ${NUMBER_OF_REQUESTS}`
    );

    console.log(
        `Successful     : ${successCount}`
    );

    console.log(
        `Failed         : ${failedRequests.length}`
    );

    console.log(
        `Success Rate   : ` +
        `${(
            successCount /
            NUMBER_OF_REQUESTS *
            100
        ).toFixed(2)}%`
    );

    console.log(
        "══════════════════════════════════════════════"
    );


    if (
        failedRequests.length === 0
    ) {

        console.log(
            "\nAll requests completed successfully."
        );

        return;
    }


    // ========================================================
    // GROUP FAILURE REASONS
    // ========================================================

    let grouped = {};


    for (
        const failure
        of failedRequests
    ) {

        const key =
            `${failure.status} | ${failure.reason}`;


        grouped[key] =
            (grouped[key] || 0) + 1;
    }


    console.log(
        "\nFAILED REQUESTS"
    );

    console.log(
        "══════════════════════════════════════════════"
    );


    Object.entries(grouped)

        .sort(
            (a, b) =>
                b[1] - a[1]
        )

        .forEach(
            ([message, count]) => {

                console.log(
                    `${count}x  ${message}`
                );
            }
        );


    console.log(
        "══════════════════════════════════════════════"
    );


    // ========================================================
    // STATUS BREAKDOWN
    // ========================================================

    grouped = {};


    for (
        const failure
        of failedRequests
    ) {

        const key =
            `${failure.status}`;


        grouped[key] =
            (grouped[key] || 0) + 1;
    }


    console.log(
        "\n📊 FAILURE BREAKDOWN"
    );


    Object.entries(grouped)

        .sort(
            (a, b) =>
                b[1] - a[1]
        )

        .forEach(
            ([status, count]) => {

                console.log(
                    `   ${status} : ${count}`
                );
            }
        );


    console.log();
}


// ============================================================
// MAIN
// ============================================================

async function main() {

    console.log(
        "=============================================="
    );

    console.log(
        "DYNAMIC FORM LOAD TEST"
    );

    console.log(
        "=============================================="
    );

    console.log(
        `Forms: ${FORM_IDS.length}`
    );


    for (
        const formId
        of FORM_IDS
    ) {

        try {

            await sendRequestsForForm(
                formId
            );

        } catch (error) {

            console.error(
                `\n❌ Failed processing form ${formId}`
            );

            console.error(
                error.message
            );
        }
    }


    console.log(
        "\n=============================================="
    );

    console.log(
        "ALL FORMS COMPLETED"
    );

    console.log(
        "=============================================="
    );
}


main();