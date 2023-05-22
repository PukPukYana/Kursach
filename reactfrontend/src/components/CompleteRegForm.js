import {Component} from "react";
import Spinner from "./Spinner";
import Api from "../Api";

class SmLinkForm extends Component {

    constructor(props, context) {
        super(props, context);

        this.state = {
            username: {
                value: "",
                status: "",
                message: "",
                spinnerActive: false
            },
            email: {
                value: "",
                status: "",
                message: "",
                spinnerActive: false,
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

    handleUsernameInput(e) {

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

                Api.validateUsername(updatedValue, abortSignal)
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

    handleEmailInput(e) {

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

                Api.validateEmail(updatedValue, abortSignal)
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

    handleSubmit(e) {

        e.preventDefault();

        if(this.state.currentStepIndex === 0) {

            if(this.state.username.status !== "success" || this.state.username.spinnerActive === true) {
                return;
            }

            this.setState({
                submitActive: false,
                submitMessage: "",
                currentStepIndex: 1
            });
        }

        if (this.state.currentStepIndex === 1) {

            if(this.state.email.status !== "success" || this.state.email.spinnerActive === true) {
                return;
            }

            const abortController = this.sendAbortSignal();

            this.setState({
                submitSpinnerActive: true
            })

            Api.completeUserRegistration(
                this.state.username.value,
                this.state.email.value,
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
                <p className="form-title">Всего несколько шагов до завершения регистрации:</p>

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
                        <label htmlFor="username">Придумайте имя пользователя</label>
                        <input id="username"
                               type="text"
                               name="username"
                               value={this.state.username.value}
                               onInput={this.handleUsernameInput.bind(this)} placeholder="username"
                               className={this.state.username.status}/>
                        <Spinner active={this.state.username.spinnerActive}/>
                        <span className={"input-message " + this.state.username.status}>{this.state.username.message}</span>
                    </div>
                </div>

                <div className="form-page" style={this.state.currentStepIndex === 1 ? {} : {display: "none"}}>
                    <div className="form-row">
                        <label htmlFor="email">Введите ваш email:</label>
                        <input id="email"
                               type="text"
                               name="email"
                               value={this.state.email.value}
                               onInput={this.handleEmailInput.bind(this)} placeholder="example@mail.com"
                               className={this.state.email.status}/>
                        <Spinner active={this.state.email.spinnerActive}/>
                        <span className={"input-message " + this.state.email.status}>{this.state.email.message}</span>
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
}

export default SmLinkForm;