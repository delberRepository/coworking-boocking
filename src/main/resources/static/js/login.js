const form = document.getElementById("login-form");
const submitButton = document.getElementById("submit-button");
const statusBox = document.getElementById("status-box");
const tokenOutput = document.getElementById("token-output");
const copyTokenButton = document.getElementById("copy-token");

function setStatus(message, type) {
    statusBox.textContent = message;
    statusBox.classList.remove("is-error", "is-success");

    if (type) {
        statusBox.classList.add(type);
    }
}

async function parseError(response) {
    const contentType = response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        const errorBody = await response.json();

        if (errorBody.fields && Array.isArray(errorBody.fields)) {
            return errorBody.fields.map((field) => `${field.field}: ${field.message}`).join("\n");
        }

        return errorBody.message || JSON.stringify(errorBody, null, 2);
    }

    return response.text();
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    submitButton.disabled = true;
    tokenOutput.value = "";
    copyTokenButton.disabled = true;
    setStatus("Validando credenciales...", "");

    const payload = {
        email: document.getElementById("email").value.trim(),
        password: document.getElementById("password").value
    };

    try {
        const response = await fetch("/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorMessage = await parseError(response);
            throw new Error(errorMessage || "No se pudo iniciar sesion");
        }

        const token = await response.text();
        tokenOutput.value = token;
        localStorage.setItem("coworking.jwt", token);
        copyTokenButton.disabled = false;
        setStatus("Login correcto. JWT guardado en localStorage con la clave coworking.jwt.", "is-success");
    } catch (error) {
        setStatus(error.message, "is-error");
    } finally {
        submitButton.disabled = false;
    }
});

copyTokenButton.addEventListener("click", async () => {
    if (!tokenOutput.value) {
        return;
    }

    await navigator.clipboard.writeText(tokenOutput.value);
    setStatus("Token copiado al portapapeles.", "is-success");
});
