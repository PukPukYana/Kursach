import withHeader from "../hoc/withHeader";
import inCage from "../hoc/withCage";
import "./css/SyncProductsPage.css";
import Spinner from "./Spinner";
import React, {useState} from "react";
import {UserConsumer} from "../contexts/UserContext";
import UniversalMessageWithLink from "./UniversalMessageWithLink";
import ErrorBoundary from "./ErrorBoundary";
import {CsrfConsumer} from "../contexts/CsrfContext";
import Api from "../Api";


function SyncProductsPage() {

    const [spinnerActive, setSpinnerActive] = useState(false);
    const [tellWhySlow, setTellWhySlow] = useState(false);
    const [showSuccessMsg, setShowSuccessMsg] = useState(false);

    let handleButtonClick = (e, csrfHeaderName, csrfToken) => {

        e.preventDefault();

        if(spinnerActive) return;

        setShowSuccessMsg(false);
        setSpinnerActive(true);

        Api.updateUserItemStacks(csrfToken, csrfHeaderName, new AbortController().signal)
            .then(data => {
                setSpinnerActive(false);
                setShowSuccessMsg(true)
            });
    }

    let handleTellWhySlowClick = (e) => {
        e.preventDefault();

        setTellWhySlow(!tellWhySlow);
    }

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

                            const {role, smLinked} = userContext.user;

                            if(role === "GUEST") {
                                return <UniversalMessageWithLink message={"Завершите регистрацию, чтобы синхронизировать список продуктов."}
                                                                 linkText={"Завершить регистрацию"}
                                                                 linkHref={"/complete"}/>;
                            }

                            if(!smLinked) {
                                return <UniversalMessageWithLink message={"Привяжите аккаунт со Сбермаркета, чтобы синхронизировать список продуктов."}
                                                                 linkText={"Привязать"}
                                                                 linkHref={"/smlink"}/>;
                            }


                            return (
                                <div className="sync-products-page">

                                    <p className="page-title">
                                        Синхронизируйте продукты, которые вы заказывали на сбермаркете!
                                    </p>

                                    <CsrfConsumer>
                                        {
                                            csrfContext => {
                                                return (
                                                    <div className="button-row">
                                                        <input type="button" className={"button" + (spinnerActive ? " blocked" : "")} value="Синхронизировать" onClick={(e) => handleButtonClick(e, csrfContext.csrfHeaderName, csrfContext.csrfToken)}/>
                                                        <Spinner active={spinnerActive}/>
                                                    </div>
                                                );
                                            }
                                        }
                                    </CsrfConsumer>

                                    {showSuccessMsg &&
                                        <UniversalMessageWithLink message={"Синхронизация завершена успешно"}
                                                                  linkText={"На главную"}
                                                                  linkHref={"/"}/>
                                    }

                                    {spinnerActive &&
                                        <div className="while-loading">
                                            <p>Процесс синхронизации пошел!</p>
                                            <img src="https://cdn.shopify.com/s/files/1/0344/6469/files/LYzZsxu_-_Imgur_grande.gif?v=1523984855" alt="" width={300}/>
                                            <a href="#" className="link" onClick={(e) => handleTellWhySlowClick(e)}>Почему это работает медленно?</a>
                                            {tellWhySlow &&
                                                <p>
                                                    API сервиса Sbermarket отвечает на некоторые виды запросов, например на запрос подробной информации
                                                    о продукта, крайне медленно. Так как SberManager вынужден, получать подробную информацию о продуктах
                                                    для того, чтобы отслеживать сроки годности продуктов, синхронизация может занять какое-то время.
                                                    Проверить, какие из купленных вами на сервисе SberMarket продукты уже синхронизировались, можно на
                                                    главной странице нашего сервиса.
                                                </p>
                                            }
                                        </div>
                                    }



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

export default inCage(withHeader(SyncProductsPage));