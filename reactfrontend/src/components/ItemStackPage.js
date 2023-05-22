import "./css/ItemStackPage.css";
import withHeader from "../hoc/withHeader";
import inCage from "../hoc/withCage";
import {useParams} from "react-router-dom";
import ErrorBoundary from "./ErrorBoundary";
import {UserConsumer} from "../contexts/UserContext";
import UniversalMessageWithLink from "./UniversalMessageWithLink";
import React, {useEffect, useState} from "react";
import Api from "../Api";
import {CsrfConsumer} from "../contexts/CsrfContext";
import Spinner from "./Spinner";

function ItemStackPage() {

    const { id } = useParams();

    const [itemStack, setItemStack] = useState(null);
    const [errorResponse, setErrorResponse] = useState(null);
    const [deleteSpinnerActive, setDeleteSpinnerActive] = useState(false);
    const [archiveSpinnerActive, setArchiveSpinnerActive] = useState(false);

    function beautifyDate(uglyDate) {
        return new Date(Date.parse(uglyDate)).toLocaleDateString("ru-RU") + ", " + uglyDate.substring(11, 16);
    }

    useEffect(function () {

        const abortController = new AbortController();

        Api.getItemStackById(id, abortController.signal)
            .then((response) => {
                if(!response["name"]) setErrorResponse(response)
                else setItemStack(response);
            });

        return () => abortController.abort();
    }, [id]);

    let handleOpenPackageClick = (e, csrfToken, csrfHeaderName) => {
        e.preventDefault();

        if(itemStack == null) return;

        Api.openItemStackById(itemStack["id"], csrfToken, csrfHeaderName, new AbortController().signal)
            .then(async response => {
                if (response.status !== 200 && response.status !== 204) {
                    console.log(await response.json())
                } else {
                    window.location.reload();
                }
            });
    }

    let handleClosePackageClick = (e, csrfToken, csrfHeaderName) => {
        e.preventDefault();

        if(itemStack == null) return;

        Api.closeItemStackById(itemStack["id"], csrfToken, csrfHeaderName, new AbortController().signal)
            .then(async response => {
                if (response.status !== 200 && response.status !== 204) {
                    console.log(await response.json());
                } else {
                    window.location.reload();
                }
            });
    }

    let handleDeleteClick = (e, csrfToken, csrfHeaderName) => {
        e.preventDefault();

        if(itemStack == null) return null;

        setDeleteSpinnerActive(true);

        Api.deleteItemStackById(itemStack["id"], csrfToken, csrfHeaderName, new AbortController().signal)
            .then(r => {
                setDeleteSpinnerActive(false);
                window.location.href = "/";  // todo handle error
            });
    }

    let handleArchiveClick = (e, archive, csrfToken, csrfHeaderName) => {
        e.preventDefault();

        if(itemStack == null) return null;

        setArchiveSpinnerActive(true);

        Api.changeItemStackArchiveStatusById(itemStack["id"], archive, csrfToken, csrfHeaderName, new AbortController().signal)
            .then(r => {
                setArchiveSpinnerActive(false);
                window.location.reload();
            });
    }

    return (
        <ErrorBoundary>
            <UserConsumer>
                {
                    userContext => {

                        if (!userContext.isLoaded) {
                            return null;
                        }

                        if (userContext.hasError) {
                            throw new Error("Не удается установить соединение с сервером, повторите попытку позднее.");
                        }

                        if(userContext.user) {

                            const { role } = userContext.user;

                            if(role === "GUEST") {
                                return <UniversalMessageWithLink message={"Завершите регистрацию, чтобы просматривать свои продукты."}
                                                                 linkText={"Завершить регистрацию"}
                                                                 linkHref={"/complete"}/>;
                            }

                            if(errorResponse) {
                                return <UniversalMessageWithLink message={errorResponse["message"] ? errorResponse["message"] : "Что-то пошло не так..."}
                                                                 linkText={"На главную"}
                                                                 linkHref={"/"}/>
                            }

                            if(!itemStack) {
                                return null;
                            }

                            const {
                                name,
                                description,
                                imageUrl,
                                active,
                                placedAt,
                                amountPerPack,
                                count,
                                opened,
                                primaryShelfLifeOption,
                                afterOpeningShelfLifeOption,
                                goesBadInPresentation,
                                secondsUntilGoesBad
                            } = itemStack;

                            return (
                                <div className="item-stack-page">
                                    <a href="#" className="image">
                                        {imageUrl ?
                                            <img alt={name} src={imageUrl}/> :
                                            <img alt={name} src="/images/image-placeholder.png"/>
                                        }
                                    </a>
                                    <div className="sections">
                                        <div className="section">
                                            <div className="name">{name}</div>
                                        </div>
                                        <div className="section">
                                            {amountPerPack && <p>Вес (объём): {amountPerPack}</p>}
                                            {count && <p>Количество: {count}</p>}
                                            <p>Произведен: {beautifyDate(placedAt).substring(0, 10)}</p>
                                            {opened != null &&
                                                <React.Fragment>
                                                    <p>Состояние: <span className="important-info">{opened ? "Вскрыт" : "Не вскрыт"}</span></p>
                                                    <CsrfConsumer>
                                                        {
                                                            csrfContext => {
                                                                return opened ?
                                                                    <a href="close" className="link" onClick={(e) => handleClosePackageClick(e, csrfContext.csrfToken, csrfContext.csrfHeaderName)}>Отметить как невскрытый</a> :
                                                                    <a href="open" className="link" onClick={(e) => handleOpenPackageClick(e, csrfContext.csrfToken, csrfContext.csrfHeaderName)}>Вскрыть</a>
                                                            }
                                                        }
                                                    </CsrfConsumer>
                                                </React.Fragment>
                                            }
                                        </div>
                                        {primaryShelfLifeOption &&
                                            <div className="section">
                                                <div className="section-title">{opened != null ? "До вскрытия" : "Хранение"}</div>
                                                {primaryShelfLifeOption["storageMode"] &&
                                                    <p>Условия хранения: {primaryShelfLifeOption["storageMode"]}</p>
                                                }
                                                {primaryShelfLifeOption["shelfLifePresentation"] &&
                                                    <p>Срок хранения: {primaryShelfLifeOption["shelfLifePresentation"]}</p>
                                                }
                                                {primaryShelfLifeOption["active"] && primaryShelfLifeOption["startCountingFrom"] &&
                                                    <p>Отсчёт начат с {beautifyDate(primaryShelfLifeOption["startCountingFrom"])}</p>
                                                }
                                                {primaryShelfLifeOption["active"] && goesBadInPresentation &&
                                                    <React.Fragment>
                                                        {secondsUntilGoesBad > 0 ?
                                                            <p>Испортится через <span className="important-info">{goesBadInPresentation}</span></p> :
                                                            <p className="important-info">Продукт испорчен {beautifyDate(primaryShelfLifeOption["itemStackGoesBadAt"])}</p>
                                                        }
                                                    </React.Fragment>
                                                }
                                            </div>
                                        }
                                        {afterOpeningShelfLifeOption &&
                                            <div className="section">
                                                <div className="section-title">После вскрытия</div>
                                                {afterOpeningShelfLifeOption["storageMode"] &&
                                                    <p>Условия хранения: {afterOpeningShelfLifeOption["storageMode"]}</p>
                                                }
                                                {afterOpeningShelfLifeOption["shelfLifePresentation"] &&
                                                    <p>Срок хранения: {afterOpeningShelfLifeOption["shelfLifePresentation"]}</p>
                                                }
                                                {afterOpeningShelfLifeOption["active"] && afterOpeningShelfLifeOption["startCountingFrom"] &&
                                                    <p>Отсчёт начат с {beautifyDate(afterOpeningShelfLifeOption["startCountingFrom"])}</p>
                                                }
                                                {afterOpeningShelfLifeOption["active"] && goesBadInPresentation &&
                                                    <p>Испортится через <span className="important-info">{goesBadInPresentation}</span></p>
                                                }
                                            </div>
                                        }
                                        {description && description.trim().length > 0 &&
                                            <div className="section">
                                                <p className="section-title">Описание</p>
                                                <p>{description}</p>
                                            </div>
                                        }
                                        <CsrfConsumer>
                                            {
                                                csrfContext => {
                                                    return (
                                                        <React.Fragment>
                                                            {active ?
                                                                <div className="section dir-row">
                                                                    <a href="archive" className="link" onClick={(e) => handleArchiveClick(e, true, csrfContext.csrfToken, csrfContext.csrfHeaderName)}>В архив</a>
                                                                    <Spinner active={archiveSpinnerActive}/>
                                                                </div> :
                                                                <div className="section dir-row">
                                                                    <a href="unarchive" className="link" onClick={(e) => handleArchiveClick(e, false, csrfContext.csrfToken, csrfContext.csrfHeaderName)}>Разархивировать</a>
                                                                    <Spinner active={archiveSpinnerActive}/>
                                                                </div>
                                                            }

                                                            <div className="section dir-row">
                                                                <a href="delete" className="link danger" onClick={(e) => handleDeleteClick(e, csrfContext.csrfToken, csrfContext.csrfHeaderName)}>Удалить</a>
                                                                <Spinner active={deleteSpinnerActive}/>
                                                            </div>
                                                        </React.Fragment>
                                                    );
                                                }
                                            }
                                        </CsrfConsumer>
                                    </div>
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

export default inCage(withHeader(ItemStackPage));