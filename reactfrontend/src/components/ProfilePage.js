import inCage from "../hoc/withCage";
import withHeader from "../hoc/withHeader";
import {UserConsumer} from "../contexts/UserContext";
import React from "react";
import "./css/ProfilePage.css";
import ProfilePageRow from "./ProfilePageRow";
import ProxiedLink from "./ProxiedLink";
import ErrorBoundary from "./ErrorBoundary";
import UniversalMessageWithLink from "./UniversalMessageWithLink";

function ProfilePage () {
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

                            const {id, username, email, phone, role} = userContext.user;

                            if(role === "GUEST") {
                                return <UniversalMessageWithLink message={"Завершите регистрацию, чтобы мы могли создать для вас профиль."}
                                                                 linkText={"Завершить регистрацию"}
                                                                 linkHref={"/complete"}/>;
                            }

                            return (
                                <div className="profile-page">
                                    <ProfilePageRow title={username} value={'#' + id} heading={true}/>
                                    <ProfilePageRow title="Email" value={email}/>
                                    <ProfilePageRow title="Аккаунт SM"
                                                    value={phone ? phone : "Привязать"}
                                                    href="/smlink"/>
                                    <ProxiedLink href="/api/logout" redirect="/" style="danger">Выйти</ProxiedLink>
                                </div>
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

export default inCage(withHeader(ProfilePage));