class ObjectValidationResult {

    _hasErrors;
    _fieldErrors;


    get hasErrors() {
        return this._hasErrors;
    }

    set hasErrors(value) {
        this._hasErrors = value;
    }

    get fieldErrors() {
        return this._fieldErrors;
    }

    set fieldErrors(value) {
        this._fieldErrors = value;
    }

    getFieldErrorMessage(fieldName) {

        if(!this._fieldErrors) return null;

        for (const index in this._fieldErrors) {
            if(this._fieldErrors[index].fieldName === fieldName) {
                return this._fieldErrors[index].errorMessage;
            }
        }

        return null;
    }

    getFieldErrorMessageByErrorCodeStartsWith(startsWith) {

        for (const index in this._fieldErrors) {
            if(this._fieldErrors[index].errorCode.startsWith(startsWith)) {
                return this._fieldErrors[index].errorMessage;
            }
        }

        return null;
    }

    removeFieldError (fieldName) {

        let updatedFieldErrors = [];

        for (const index in this._fieldErrors) {
            if(this._fieldErrors[index].fieldName !== fieldName) {
                updatedFieldErrors.push(this._fieldErrors[index]);
            }
        }
    }
}

export class FieldError {

    _fieldName;
    _errorCode;
    _errorMessage;


    constructor(fieldName, errorCode, errorMessage) {
        this._fieldName = fieldName;
        this._errorCode = errorCode;
        this._errorMessage = errorMessage;
    }

    get fieldName() {
        return this._fieldName;
    }

    set fieldName(value) {
        this._fieldName = value;
    }

    get errorCode() {
        return this._errorCode;
    }

    set errorCode(value) {
        this._errorCode = value;
    }

    get errorMessage() {
        return this._errorMessage;
    }

    set errorMessage(value) {
        this._errorMessage = value;
    }
}

export default ObjectValidationResult;