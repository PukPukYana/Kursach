import withHeader from "../hoc/withHeader";
import inCage from "../hoc/withCage";
import ErrorBoundary from "./ErrorBoundary";
import {UserConsumer} from "../contexts/UserContext";
import React from "react";
import "./css/LoginPage.css";
import UniversalMessageWithLink from "./UniversalMessageWithLink";

function LoginPage () {

    return (
        <ErrorBoundary>
            <UserConsumer>
                {
                    userContext => {

                        if(!userContext.isLoaded) {
                            return null;
                        }

                        if(userContext.hasError) {
                            throw new Error("Не удается установить соединение с сервером, повторите попытку позднее.");
                        }

                        if(userContext.user) {
                            return <UniversalMessageWithLink message={"Вы уже вошли в свой аккаунт."}
                                                             linkText={"На главную"}
                                                             linkHref={"/"}/>;
                        }

                        return (
                            <div className="login-greeting-container">
                                <div className="greeting">Велкам ту зе <span className="greeting-site-name">SberManager!</span></div>
                                <p className="login-regular-line">Кажется, вы не вошли в аккаунт <span className="emoji">👀</span></p>

                                <p className="github-login login-link">
                                    <a href="/oauth2/authorization/google" className="login-link">
                                        <img src="/images/logos/google.png" alt="google.com"/>
                                        Войти с помощью Google
                                    </a>
                                </p>
                            </div>
                        );

                    }
                }
            </UserConsumer>
        </ErrorBoundary>
    );
}

export default inCage(withHeader(LoginPage));