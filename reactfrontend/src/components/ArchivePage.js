import {UserConsumer} from "../contexts/UserContext";
import React, {useEffect, useState} from "react";
import withHeader from "../hoc/withHeader";
import inCage from "../hoc/withCage";
import ErrorBoundary from "./ErrorBoundary";
import Api from "../Api";
import "./css/HomePage.css";

function ArchivePage() {

    const [itemStacks, setItemStacks] = useState([]);

    useEffect(() => {

        let abortController = new AbortController();

        Api.getAllItemStacks(false, abortController.signal)
            .then(response => {
                if(response["itemStacks"]) {
                    setItemStacks(response["itemStacks"]);
                }
            });

        return () => abortController.abort();
    }, [])

    return (
        <ErrorBoundary>
            <UserConsumer>
                {
                    (userContext => {

                        if(!userContext.isLoaded) {
                            return null;
                        }

                        if(userContext.hasError) {
                            throw new Error("Не удается установить соединение с сервером, повторите попытку позднее.")
                        }

                        if(!userContext.user) {
                            return (
                                <div>
                                    <a href="/login" className="link">Войдите </a>
                                    в аккаунт, чтобы пользоваться нашим прекрасынм сервисом.
                                </div>
                            );
                        }

                        if(itemStacks.length === 0) {
                            return (
                                <div>
                                    <div className="page-title">Ваш архив пуст.</div>
                                    <div className="links-row">
                                        <a href="/" className="link">На главную</a>
                                    </div>
                                </div>
                            );
                        }

                        return (
                            <React.Fragment>
                                <div>
                                    <div className="page-title">Это список ваших продуктов, находящихся в архиве</div>
                                    <div className="links-row">
                                        <a href="/" className="link">На главную</a>
                                    </div>
                                </div>

                                <div>
                                    <div className="products-table">
                                        <div className="table-row heading">
                                            <div>Имя</div>
                                            <div>Кол-во</div>
                                            <div>Испортится через</div>
                                        </div>

                                        {
                                            itemStacks.reverse().map((itemStack) => {

                                                let isExpired = itemStack["secondsUntilGoesBad"] && itemStack["secondsUntilGoesBad"] < 0;

                                                return (
                                                    <a className={"table-row archive" + (isExpired ? " expired" : "")} href={"/item-stack/" + itemStack["id"]}>
                                                        <div>{itemStack["name"]}</div>
                                                        <div>{itemStack["count"]}{itemStack["amountPerPack"] ? " × " + itemStack["amountPerPack"] : ""}</div>
                                                        <div>{itemStack["goesBadInPresentation"] ? itemStack["goesBadInPresentation"] : "∞"}</div>
                                                    </a>
                                                );
                                            })
                                        }
                                    </div>
                                </div>
                            </React.Fragment>
                        );
                    })
                }
            </UserConsumer>
        </ErrorBoundary>
    );
}

export default inCage(withHeader(ArchivePage));