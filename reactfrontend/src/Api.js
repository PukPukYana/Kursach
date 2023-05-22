import ValidationResult from "./ValidationResult";
import ObjectValidationResult, {FieldError} from "./ObjectValidationResult";

let Api = (function () {

    let URL_ROOT = "/api";

    async function buildObjectValidationResult(response) {

        let objectValidationResult = new ObjectValidationResult();

        if(response.status === 200) {
            objectValidationResult.hasErrors = false;
        }

        if(response.status === 400) {

            objectValidationResult.hasErrors = true;

            const responseData = await response.json();

            let fieldErrors = [];

            for(const index in responseData["invalidFields"]) {
                fieldErrors.push(
                    new FieldError(
                        responseData["invalidFields"][index]["fieldName"],
                        responseData["invalidFields"][index]["errorCode"],
                        responseData["invalidFields"][index]["errorMessage"]
                    )
                );
            }

            objectValidationResult.fieldErrors = fieldErrors;
        }

        return objectValidationResult;
    }

    async function buildValidationResult(response, fieldName) {

        let validationResult = new ValidationResult();

        if(response.status === 200) {
            validationResult.hasErrors = false;
        }

        if(response.status === 400) {
            validationResult.hasErrors = true;

            const responseData = await response.json();

            let errors = [];

            for(const index in responseData["invalidFields"]) {
                if(responseData["invalidFields"][index]["fieldName"] === fieldName) {
                    errors.push(responseData["invalidFields"][index]["errorMessage"]);
                }
            }

            validationResult.errors = errors;
        }

        if(response.status >= 500) {
            validationResult.hasErrors = true;
            validationResult.errors = ["На сервере какие-то проблемы, повторите попытку позже"];
        }

        return validationResult;
    }

    return {

        validatePhone: async function (phone, abortSignal) {

            const response =  await fetch(
                URL_ROOT + "/user/phone/validate?phone=" + encodeURIComponent(phone),
                {
                    signal: abortSignal
                }
            );

            return buildValidationResult(response, "phone");
        },

        sendSmsCode: async function (encryptedPhone, csrfToken, csrfHeaderName, abortSignal) {

            const response = await fetch(
                URL_ROOT + "/sm/send_code",
                {
                    signal: abortSignal,
                    method: "POST",
                    body: JSON.stringify({encryptedPhone}),
                    headers: {
                        "Content-Type": "application/json",
                        [csrfHeaderName]: csrfToken
                    }
                }
            );

            return await response.json();
        },

        confirmPhoneNumber: async function (rawPhone,
                                            encryptedPhone,
                                            confirmationCode,
                                            csrfToken,
                                            csrfHeaderName,
                                            abortSignal) {

            const response = await fetch(
                URL_ROOT + "/sm/auth",
                {
                    signal: abortSignal,
                    method: "POST",
                    body: JSON.stringify({
                        rawPhone, encryptedPhone, confirmationCode
                    }),
                    headers: {
                        "Content-Type": "application/json",
                        [csrfHeaderName]: csrfToken
                    }
                }
            );

            if(response.status === 200 || response.status === 204) {
                return {};
            }

            return await response.json();
        },

        validateUsername: async function (username, abortSignal) {

            const response =  await fetch(
                URL_ROOT + "/user/username/validate?username=" + encodeURIComponent(username),
                {
                    signal: abortSignal
                }
            );

            return buildValidationResult(response, "username");
        },

        validateEmail: async function (email, abortSignal) {

            const response =  await fetch(
                URL_ROOT + "/user/email/validate?email=" + encodeURIComponent(email),
                {
                    signal: abortSignal
                }
            );

            return buildValidationResult(response, "email");
        },

        completeUserRegistration: async function (username, email, csrfToken, csrfHeaderName, abortSignal) {

            const response = await fetch(
                URL_ROOT + "/user",
                {
                    signal: abortSignal,
                    method: "post",
                    body: JSON.stringify({username, email}),
                    headers: {
                        "Content-Type": "application/json",
                        [csrfHeaderName]: csrfToken
                    }
                }
            );

            if(response.status === 200 || response.status === 204) {
                return {};
            }

            return await response.json();
        },

        updateUserItemStacks: async function (csrfToken, csrfHeaderName, abortSignal) {

            const response = await fetch(
                URL_ROOT + "/item_stack/update",
                {
                    signal: abortSignal,
                    method: "POST",
                    headers: {
                        [csrfHeaderName]: csrfToken
                    }
                }
            );

            if(response.status === 200 || response.status === 204) {
                return {};
            }

            return await response.json();
        },

        getItemStackById: async function (id, abortSignal) {

            const response = await fetch(
                URL_ROOT + "/item_stack/" + id,
                {
                    signal: abortSignal
                }
            );

            return await response.json();
        },

        createItemStack: async function (createItemStackRequestDto, csrfToken, csrfHeaderName, abortSignal) {

            const response = await fetch(
                URL_ROOT + "/item_stack",
                {
                    signal: abortSignal,
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        [csrfHeaderName]: csrfToken,
                    },
                    body: JSON.stringify(createItemStackRequestDto)
                }
            );

            if(response.status === 200 || response.status === 204) {
                return await response.json();
            }

            return buildObjectValidationResult(response);
        },

        getAllItemStacks: async function(active, abortSignal) {
            const response =  await fetch(
                URL_ROOT + "/item_stack?active=" + String(active),
                {
                    signal: abortSignal
                }
            );

            return await response.json();
        },

        deleteItemStackById: async function (id, csrfToken, csrfHeaderName, abortSignal) {

            await fetch(
                URL_ROOT + "/item_stack/" + encodeURIComponent(id),
                {
                    signal: abortSignal,
                    method: "DELETE",
                    headers: {
                        [csrfHeaderName]: csrfToken,
                    },
                }
            );
        },

        openItemStackById: async function (id, csrfToken, csrfHeaderName, abortSignal) {

            return await fetch(
                URL_ROOT + "/item_stack/" + encodeURIComponent(id) + "/open",
                {
                    signal: abortSignal,
                    method: "POST",
                    headers: {
                        [csrfHeaderName]: csrfToken,
                    },
                }
            );
        },

        closeItemStackById: async function (id, csrfToken, csrfHeaderName, abortSignal) {

            return await fetch(
                URL_ROOT + "/item_stack/" + encodeURIComponent(id) + "/close",
                {
                    signal: abortSignal,
                    method: "POST",
                    headers: {
                        [csrfHeaderName]: csrfToken,
                    },
                }
            );
        },

        changeItemStackArchiveStatusById: async function (id, archive, csrfToken, csrfHeaderName, abortSignal) {

            let url = URL_ROOT + "/item_stack/" + encodeURIComponent(id) + "/" + (archive ? "archive" : "unarchive");

            return await fetch(
                url,
                {
                    signal: abortSignal,
                    method: "POST",
                    headers: {
                        [csrfHeaderName]: csrfToken,
                    },
                }
            );
        }
    };
})();

export default Api;