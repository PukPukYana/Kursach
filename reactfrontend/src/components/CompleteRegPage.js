import "./css/SmLinkPage.css";
import {UserConsumer} from "../contexts/UserContext";
import ErrorBoundary from "./ErrorBoundary";
import React from "react";
import withHeader from "../hoc/withHeader";
import inCage from "../hoc/withCage";
import Api from "../Api";
import SmLinkForm from "./SmLinkForm";
import {CsrfConsumer} from "../contexts/CsrfContext";
import CompleteRegForm from "./CompleteRegForm";
import UniversalMessageWithLink from "./UniversalMessageWithLink";

window.dynamicEnvsFromServer = {
    CIPHER_KEY_PHONE: "TjWnZr4u7xsAgDtGlKaPRgUkXp2s5v86sgdte5kc6uu=",
};

function SmLinkPage () {

    return (
        <ErrorBoundary>
            <UserConsumer>
                {
                    userContext => {

                        if(!userContext.isLoaded) {
                            return null;
                        }

                        if(userContext.hasError) {
                            throw new Error("Не удается установить соединение с сервером, повторите попытку позднее.")
                        }

                        if(userContext.user) {

                            const {id, username, email, phone, role} = userContext.user;

                            if(role !== "GUEST") {
                                return <UniversalMessageWithLink message={"Вы уже прошли полную регистрацию."}
                                                                 linkText={"На главную"}
                                                                 linkHref={"/"}/>;
                            }

                            return (
                                <CsrfConsumer>
                                    {
                                        csrfContext => {
                                            return <CompleteRegForm csrfToken={csrfContext.csrfToken}
                                                                    csrfHeaderName={csrfContext.csrfHeaderName}/>;
                                        }
                                    }
                                </CsrfConsumer>
                            );
                        }

                        return (
                            <p>
                                <a href="/login" className="link">Войдите </a>
                                в аккаунт, чтобы пользоваться нашим прекрасынм сервисом.
                            </p>
                        );
                    }
                }
            </UserConsumer>
        </ErrorBoundary>
    );
}

export default inCage(withHeader(SmLinkPage));