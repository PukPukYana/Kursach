class ValidationResult {

    _hasErrors;
    _errors;


    get hasErrors() {
        return this._hasErrors;
    }

    set hasErrors(value) {
        this._hasErrors = value;
    }

    get errors() {
        return this._errors;
    }

    set errors(value) {
        this._errors = value;
    }
}

export default ValidationResult;