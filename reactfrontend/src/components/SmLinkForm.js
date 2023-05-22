import {Component} from "react";
import Spinner from "./Spinner";
import "./css//SmLinkForm.css";
import Api from "../Api";

class SmLinkForm extends Component {

    constructor(props, context) {
        super(props, context);

        this.state = {
            phone: {
                value: "",
                status: "",
                message: "",
                spinnerActive: false
            },
            code: {
                value: "",
                status: "",
                message: "",
                spinnerActive: false,
                requiredLength: 6
            },
            abortController: new AbortController(),
            submitActive: false,
            submitMessage: "",
            currentStepIndex: 0,
            finalStepIndex: 1,
            submitSpinnerActive: false
        }

        this.csrfToken = props.csrfToken;
        this.csrfHeaderName = props.csrfHeaderName;
    }

    sendAbortSignal() {
        this.state.abortController.abort();
        const abortController = new AbortController();
        this.setState({abortController});
        return abortController;
    }

    handleInput(e) {

        e.preventDefault();

        const name = e.target.name;

        this.setState({
            [name]: {
                ...this.state[name],
                value: e.target.value,
                message: "",
                status: "",
            }
        });

        const abortController = this.sendAbortSignal();

        return new Promise((resolve) => {
            resolve(abortController.signal);
        });
    }

    handlePhoneInput(e) {

        const name = e.target.name;
        const updatedValue = e.target.value;

        this.handleInput(e)
            .then((abortSignal) => {

                if(updatedValue.length === 0) {
                    this.setState({
                        submitActive: false
                    });
                    return;
                }

                this.setState({
                    [name]: {
                        ...this.state[name],
                        spinnerActive: true,
                    },
                    submitActive: false
                });

                Api.validatePhone(updatedValue, abortSignal)
                    .then((validationResult) => {

                        let status = "success";
                        let message = "✓";

                        if(validationResult.hasErrors) {
                            status = "danger";
                            message = validationResult.errors[0];
                        }

                        this.setState({
                            [name]: {
                                ...this.state[name],
                                spinnerActive: false,
                                status,
                                message
                            },
                            submitActive: status === "success"
                        });

                    })
                    .catch(e => {
                        if(e.name !== "AbortError") throw e;
                    });
            });
    }

    handleCodeInput(e) {

        const updatedValue = e.target.value;

        this.handleInput(e)
            .then(() => {

                if(updatedValue.length === 0) {
                    this.setState({
                        submitActive: false
                    });
                    return;
                }

                if(updatedValue.length !== this.state.code.requiredLength) {
                    this.setState({
                        code: {
                            ...this.state.code,
                            message: "Должен иметь длину " + this.state.code.requiredLength + " символов",
                            status: "danger"
                        },
                        submitActive: false
                    });
                } else {
                    this.setState({
                        code: {
                            ...this.state.code,
                            message: "✓",
                            status: "success"
                        },
                        submitActive: true
                    });
                }
            });
    }

    handleSubmit(e) {

        e.preventDefault();

        if(this.state.currentStepIndex === 0) {

            if(this.state.phone.status !== "success" || this.state.phone.spinnerActive === true) {
                return;
            }

            let formattedPhone = this.#formatRawPhone(this.state.phone.value);
            let encryptedPhone = window.encryptPhone(formattedPhone);

            const abortController = this.sendAbortSignal();

            Api.sendSmsCode(encryptedPhone, this.csrfToken, this.csrfHeaderName, abortController.signal)
                .then(data => {

                    if(data["codeLength"]) {
                        this.setState({
                            code: {
                                ...this.state.code,
                                requiredLength: data["codeLength"]
                            },
                            currentStepIndex: 1,
                            submitActive: false,
                            submitMessage: ""
                        });
                    } else if (data["exceptionCode"]) {
                        this.setState({
                            submitMessage: data["message"]
                        });
                    }
                });
        }

        if (this.state.currentStepIndex === 1) {

            if(this.state.code.status !== "success" || this.state.code.spinnerActive === true) {
                return;
            }

            let formattedPhone = this.#formatRawPhone(this.state.phone.value);
            let encryptedPhone = window.encryptPhone(formattedPhone);

            const abortController = this.sendAbortSignal();

            this.setState({
                submitSpinnerActive: true
            })

            Api.confirmPhoneNumber(
                this.state.phone.value,
                encryptedPhone,
                this.state.code.value,
                this.csrfToken,
                this.csrfHeaderName,
                abortController.signal
            ).then((data) => {

                this.setState({
                    submitSpinnerActive: false
                });

                if(data["exceptionCode"]) {
                    this.setState({
                        code: {
                            ...this.state.code,
                            status: "danger",
                            message: data["message"]
                        },
                        submitActive: false,
                    });
                } else {
                    window.location.href = "/profile";
                }
            });
        }
    }

    handleStepBack() {
        this.setState({
            currentStepIndex: this.state.currentStepIndex - 1,
            submitMessage: "",
            submitActive: true,
            code: {
                ...this.state.code,
                value: "",
                status: ""
            }
        });
    }

    render() {
        return (
            <form action="/api/sm/auth" onSubmit={this.handleSubmit.bind(this)}>
                <p className="form-title">Используйте возможности сервиса по-полной!</p>

                <p className="form-step-counter"
                   style={this.state.finalStepIndex > 0 ? {} : {display: "none"}}>
                    Шаг {this.state.currentStepIndex + 1} из {this.state.finalStepIndex + 1}
                    <a style={this.state.currentStepIndex > 0 ? {} : {display: "none"}}
                       href="#"
                       className="link-step-back"
                       onClick={this.handleStepBack.bind(this)}>
                        Назад к шагу {this.state.currentStepIndex}
                    </a>
                </p>

                <div className="form-page" style={this.state.currentStepIndex === 0 ? {} : {display: "none"}}>
                    <div className="form-row">
                        <label htmlFor="phone">Номер телефона на Сбермаркете:</label>
                        <input id="phone"
                               type="text"
                               name="phone"
                               value={this.state.phone.value}
                               onInput={this.handlePhoneInput.bind(this)} placeholder="81234567890"
                               className={this.state.phone.status}/>
                        <Spinner active={this.state.phone.spinnerActive}/>
                        <span className={"input-message " + this.state.phone.status}>{this.state.phone.message}</span>
                    </div>
                </div>

                <div className="form-page" style={this.state.currentStepIndex === 1 ? {} : {display: "none"}}>
                    <div className="form-row">
                        <label htmlFor="code">Код из SMS:</label>
                        <input id="code"
                               type="text"
                               name="code"
                               value={this.state.code.value}
                               onInput={this.handleCodeInput.bind(this)} placeholder="123456"
                               className={this.state.code.status}/>
                        <Spinner active={this.state.code.spinnerActive}/>
                        <span className={"input-message " + this.state.code.status}>{this.state.code.message}</span>
                    </div>
                </div>

                <div className="submit-button-row">
                    <input className={"button" + (this.state.submitActive ? "" : " blocked")}
                           type="submit"
                           value={this.state.currentStepIndex === this.state.finalStepIndex ? "Отправить" : "Далее"}/>
                    <Spinner active={this.state.submitSpinnerActive}/>
                    <span id="submit-button-message" className="submit-button-message danger">
                        {this.state.submitMessage}
                    </span>
                </div>
            </form>
        );
    }

    #formatRawPhone(rawPhone) {

        if(rawPhone.substring(0, 1) === '+') {
            return rawPhone.substring(1);
        }

        return rawPhone.replace('8', '7');
    }
}

export default SmLinkForm;