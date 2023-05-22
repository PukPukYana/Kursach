import withHeader from "../hoc/withHeader";
import inCage from "../hoc/withCage";
import ErrorBoundary from "./ErrorBoundary";
import {UserConsumer} from "../contexts/UserContext";
import UniversalMessageWithLink from "./UniversalMessageWithLink";
import React, {useState} from "react";
import "./css/CreateItemStackPage.css";
import Api from "../Api";
import {CsrfConsumer} from "../contexts/CsrfContext";

function CreateItemStackPage() {

    const [primaryStorageInfoAdded, setPrimaryStorageInfoAdded] = useState(false);
    const [afterOpeningStorageInfoAdded, setAfterOpeningStorageInfoAdded] = useState(false);

    const [name, setName] = useState("");
    const [volume, setVolume] = useState("");
    const [count, setCount] = useState("");
    const [imageUrl, setImageUrl] = useState("");
    const [description, setDescription] = useState("");

    const [placedDay, setPlacedDay] = useState("");
    const [placedMonth, setPlacedMonth] = useState("");
    const [placedYear, setPlacedYear] = useState("");

    const [primaryStorageMode, setPrimaryStorageMode] = useState("");
    const [primaryShelfLife, setPrimaryShelfLife] = useState("");
    const [primaryShelfLifeMeasure, setPrimaryShelfLifeMeasure] = useState("-");

    const [afterOpeningStorageMode, setAfterOpeningStorageMode] = useState("");
    const [afterOpeningShelfLife, setAfterOpeningShelfLife] = useState("");
    const [afterOpeningShelfLifeMeasure, setAfterOpeningShelfLifeMeasure] = useState("-");

    const [fieldErrors, setFieldErrors] = useState([]);

    let handleNameInput = (e) => {
        setName(e.target.value);
        removeFieldError("name");
    };
    let handleVolumeInput = (e) => {
        setVolume(e.target.value);
        removeFieldError("amountPerPack");
    };
    let handleCountInput = (e) => {
        setCount(e.target.value);
        removeFieldError("count");
    };
    let handleImageUrlInput = (e) => {
        setImageUrl(e.target.value);
        removeFieldError("imageUrl");
    }
    let handleDescriptionInput = (e) => {
        setDescription(e.target.value);
        removeFieldError("description");
    };

    let handlePlacedDayInput = (e) => {
        setPlacedDay(e.target.value);
        removeFieldError("placedAt");
    };
    let handlePlacedMonthInput = (e) => {
        setPlacedMonth(e.target.value);
        removeFieldError("placedAt");
    };
    let handlePlacedYearInput = (e) => {
        setPlacedYear(e.target.value);
        removeFieldError("placedAt");
    };

    let handlePrimaryStorageModeInput = (e) => {
        setPrimaryStorageMode(e.target.value);
        removeFieldErrorByErrorCodeStartsWith("primaryItemStackShelfLifeOption.storageMode");
    };
    let handlePrimaryShelfLifeInput = (e) => {
        setPrimaryShelfLife(e.target.value);
        removeFieldErrorByErrorCodeStartsWith("primaryItemStackShelfLifeOption.shelfLifePresentation");
    };
    let handlePrimaryShelfLifeMeasureInput = (e) => {
        setPrimaryShelfLifeMeasure(e.target.value);
        removeFieldErrorByErrorCodeStartsWith("primaryItemStackShelfLifeOption.shelfLifePresentation");
    };

    let handleAfterOpeningStorageModeInput = (e) => {
        setAfterOpeningStorageMode(e.target.value);
        removeFieldErrorByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.storageMode");
    };
    let handleAfterOpeningShelfLifeInput = (e) => {
        setAfterOpeningShelfLife(e.target.value);
        removeFieldErrorByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.shelfLifePresentation");
    };
    let handleAfterOpeningShelfLifeMeasureInput = (e) => {
        setAfterOpeningShelfLifeMeasure(e.target.value);
        removeFieldErrorByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.shelfLifePresentation");
    };

    let handleAddPrimaryStorageInfo = (e) => {
        e.preventDefault();
        setPrimaryStorageInfoAdded(true);
    }

    let handleRemovePrimaryStorageInfo = (e) => {
        e.preventDefault();
        setPrimaryStorageMode("")
        setPrimaryShelfLife("");
        setPrimaryShelfLifeMeasure("-");
        setPrimaryStorageInfoAdded(false);
        removeFieldErrorByErrorCodeStartsWithArray([
            "primaryItemStackShelfLifeOption.storageMode", "primaryItemStackShelfLifeOption.shelfLifePresentation"
        ]);
    }

    let handleAddAfterOpeningStorageInfo = (e) => {
        e.preventDefault();
        setAfterOpeningStorageInfoAdded(true);
    }

    let handleRemoveAfterOpeningStorageInfo = (e) => {
        e.preventDefault();
        setAfterOpeningStorageMode("");
        setAfterOpeningShelfLife("");
        setAfterOpeningShelfLifeMeasure("-");
        setAfterOpeningStorageInfoAdded(false);
        removeFieldErrorByErrorCodeStartsWithArray([
            "afterOpeningItemStackShelfLifeOption.storageMode", "afterOpeningItemStackShelfLifeOption.shelfLifePresentation"
        ]);
    }

    let handleSubmit = (e, csrfHeaderName, csrfToken) => {
        e.preventDefault();

        let primaryShelfLifePresentation = primaryShelfLife + " "
            + (primaryShelfLifeMeasure === "-" ? "" : primaryShelfLifeMeasure);

        let afterOpeningShelfLifePresentation = afterOpeningShelfLife + " "
            + (afterOpeningShelfLifeMeasure === "-" ? "" : afterOpeningShelfLifeMeasure);

        let itemStack = {
            name: name.trim().length > 0 ? name.trim() : null,
            amountPerPack: volume.trim().length > 0 ? volume.trim() : null,
            count: count.trim().length > 0 ? Number(count.trim()) : null,
            imageUrl: imageUrl.trim().length > 0 ? imageUrl.trim() : null,
            description: description.trim().length > 0 ? description.trim() : null,
            placedAtDay: placedDay.trim().length > 0 ? placedDay.trim() : null,
            placedAtMonth: placedMonth.trim().length > 0 ? placedMonth.trim() : null,
            placedAtYear: placedYear.trim().length > 0 ? placedYear.trim() : null,
            primaryStorageMode: primaryStorageMode.trim().length > 0 ? primaryStorageMode.trim() : null,
            primaryShelfLifePresentation: primaryShelfLifePresentation.trim().length > 0 ?
                primaryShelfLifePresentation : null,
            afterOpeningStorageMode: afterOpeningStorageMode.trim().length > 0 ? afterOpeningStorageMode.trim() : null,
            afterOpeningShelfLifePresentation: afterOpeningShelfLifePresentation.trim().length > 0 ?
                afterOpeningShelfLifePresentation: null
        }

        Api.createItemStack(itemStack, csrfToken, csrfHeaderName, new AbortController().signal)
            .then(response => {
                console.log(response);  // todo remove

                if(response.hasErrors) {
                    setFieldErrors(response.fieldErrors);
                } else {
                    window.location.href = "/item-stack/" + response.id;
                }
            });
    }

    let getFieldErrorMessage = (fieldName) => {

        for (const index in fieldErrors) {
            if(fieldErrors[index].fieldName === fieldName) {
                return fieldErrors[index].errorMessage;
            }
        }

        return null;
    }

    let getFieldErrorMessageByErrorCodeStartsWith = (startsWith) => {

        for (const index in fieldErrors) {
            if(fieldErrors[index].errorCode.startsWith(startsWith)) {
                return fieldErrors[index].errorMessage;
            }
        }

        return null;
    }

    let removeFieldError = (fieldName) => {

        let updatedFieldErrors = [];

        for (const index in fieldErrors) {
            if(fieldErrors[index].fieldName !== fieldName) {
                updatedFieldErrors.push(fieldErrors[index]);
            }
        }

        setFieldErrors(updatedFieldErrors);
    }

    let removeFieldErrorByErrorCodeStartsWith = (startsWith) => {

        let updatedFieldErrors = [];

        for (const index in fieldErrors) {
            if(!fieldErrors[index].errorCode.startsWith(startsWith)) {
                updatedFieldErrors.push(fieldErrors[index]);
            }
        }

        setFieldErrors(updatedFieldErrors);
    }

    let removeFieldErrorByErrorCodeStartsWithArray = (startsWithArray) => {

        let updatedFieldErrors = [];

        for (const index in fieldErrors) {

            let valid = true;

            for(const i in startsWithArray) {
                if(fieldErrors[index].errorCode.startsWith(startsWithArray[i])) {
                    valid = false;
                    break;
                }
            }

            if(valid) {
                updatedFieldErrors.push(fieldErrors[index]);
            }
        }

        setFieldErrors(updatedFieldErrors);
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

                        if (!userContext.user) {
                            return (
                                <p>
                                    <a href="/login" className="link">Войдите </a>
                                    в аккаунт, чтобы пользоваться нашим прекрасынм сервисом.
                                </p>
                            );
                        }

                        if(userContext.user.role === "GUEST") {
                            return <UniversalMessageWithLink message={"Завершите регистрацию, чтобы добавлять продукты."}
                                                             linkText={"Завершить регистрацию"}
                                                             linkHref={"/complete"}/>;
                        }

                        return (
                            <div className="create-item-stack-page">
                                <div className="page-title">Создание продукта</div>
                                <CsrfConsumer>
                                    {
                                        csrfContext => {
                                            return (
                                                <form action="#"
                                                      className="create-item-stack-form"
                                                      onSubmit={(e) => handleSubmit(e, csrfContext.csrfHeaderName, csrfContext.csrfToken)}>
                                                    <div className="row">
                                                        <div className="title"><label htmlFor="name">Имя</label></div>
                                                        <input className={getFieldErrorMessage("name") ? "danger" : ""}
                                                               id="name"
                                                               type="text"
                                                               name="name"
                                                               value={name}
                                                               placeholder="Имя продукта"
                                                               onInput={(e) => handleNameInput(e)}/>
                                                        {getFieldErrorMessage("name") && <p className="danger">{getFieldErrorMessage("name")}</p>}

                                                    </div>
                                                    <div className="row">
                                                        <div className="title"><label htmlFor="volume">Вес (объём)</label></div>
                                                        <input className={getFieldErrorMessage("amountPerPack") ? "danger" : ""}
                                                               id="volume"
                                                               type="text"
                                                               name="volume"
                                                               value={volume}
                                                               placeholder="950г"
                                                               onInput={(e) => handleVolumeInput(e)}/>
                                                        {getFieldErrorMessage("amountPerPack") && <p className="danger">{getFieldErrorMessage("amountPerPack")}</p>}
                                                    </div>
                                                    <div className="row">
                                                        <div className="title"><label htmlFor="count">Количество</label></div>
                                                        <input className={getFieldErrorMessage("count") ? "danger" : ""}
                                                               id="count"
                                                               type="text"
                                                               name="count"
                                                               value={count}
                                                               placeholder="2"
                                                               onInput={(e) => handleCountInput(e)}/>
                                                        {getFieldErrorMessage("count") && <p className="danger">{getFieldErrorMessage("count")}</p>}
                                                    </div>
                                                    <div className="row">
                                                        <div className="title"><label htmlFor="">Ссылка на изображение</label></div>
                                                        <input className={getFieldErrorMessage("imageUrl") ? "danger" : ""}
                                                               id="count"
                                                               type="text"
                                                               name="imageUrl"
                                                               value={imageUrl}
                                                               placeholder="https://site.com/image.png"
                                                               onInput={(e) => handleImageUrlInput(e)}/>
                                                        {getFieldErrorMessage("imageUrl") && <p className="danger">{getFieldErrorMessage("imageUrl")}</p>}
                                                    </div>
                                                    <div className="row">
                                                        <div className="title"><label htmlFor="placed-day">Произведен</label></div>
                                                        <input className={getFieldErrorMessage("placedAt") ? "danger short" : "short"}
                                                               id="placed-day"
                                                               type="text"
                                                               name="count"
                                                               value={placedDay}
                                                               placeholder="31"
                                                               onInput={(e) => handlePlacedDayInput(e)}/>
                                                        <input className={getFieldErrorMessage("placedAt") ? "danger short" : "short"}
                                                               id="placed-month"
                                                               type="text"
                                                               name="count"
                                                               value={placedMonth}
                                                               placeholder="12"
                                                               onInput={(e) => handlePlacedMonthInput(e)}/>
                                                        <input className={getFieldErrorMessage("placedAt") ? "danger short" : "short"}
                                                               id="placed-year"
                                                               type="text"
                                                               name="count"
                                                               value={placedYear}
                                                               placeholder="2023"
                                                               onInput={(e) => handlePlacedYearInput(e)}/>
                                                        {getFieldErrorMessage("placedAt") && <p className="danger">{getFieldErrorMessage("placedAt")}</p>}
                                                    </div>
                                                    <div className="row">
                                                        <div className="title"><label htmlFor="description">Описание</label></div>
                                                        <textarea className={getFieldErrorMessage("description") ? "danger" : ""}
                                                                  name="description"
                                                                  id="description"
                                                                  cols="30"
                                                                  rows="10"
                                                                  placeholder="Оч крутой продукт"
                                                                  onInput={(e) => handleDescriptionInput(e)}>
                                                            {primaryStorageMode}
                                                        </textarea>
                                                        {getFieldErrorMessage("description") && <p className="danger">{getFieldErrorMessage("description")}</p>}
                                                    </div>

                                                    <div className="text-divider">
                                                        <div className="title">До вскрытия</div>
                                                        {!primaryStorageInfoAdded &&
                                                            <p>Добавьте информацию о хранении продукта до вскрытия упаковки, чтобы мы могли помочь вам следить за его свежестью!</p>
                                                        }
                                                    </div>

                                                    {!primaryStorageInfoAdded &&
                                                        <div className="row">
                                                            <div className="title"></div>
                                                            <a href="#" className="link" onClick={(e) => handleAddPrimaryStorageInfo(e)}>Добавить</a>
                                                        </div>
                                                    }

                                                    {primaryStorageInfoAdded &&
                                                        <React.Fragment>
                                                            <div className="row">
                                                                <div className="title"><label htmlFor="primary-storage-mode">Условия хранения</label></div>
                                                                <textarea className={getFieldErrorMessageByErrorCodeStartsWith("primaryItemStackShelfLifeOption.storageMode") ? "danger" : null}
                                                                          name="primary-storage-mode"
                                                                          id="primary-storage-mode"
                                                                          placeholder="При температуре 0°С до +25°С"
                                                                          onInput={(e) => handlePrimaryStorageModeInput(e)}>
                                                                    {primaryStorageMode}
                                                                </textarea>
                                                                {getFieldErrorMessageByErrorCodeStartsWith("primaryItemStackShelfLifeOption.storageMode") && <p className="danger">{getFieldErrorMessageByErrorCodeStartsWith("primaryItemStackShelfLifeOption.storageMode")}</p>}
                                                            </div>
                                                            <div className="row">
                                                                <div className="title"><label htmlFor="primary-shelf-life">Срок
                                                                    годности</label></div>
                                                                <input className={getFieldErrorMessageByErrorCodeStartsWith("primaryItemStackShelfLifeOption.shelfLifePresentation") ? "danger short" : "short"}
                                                                       id="primary-shelf-life"
                                                                       type="text"
                                                                       name="primary-shelf-life"
                                                                       value={primaryShelfLife}
                                                                       placeholder="30"
                                                                       onInput={(e) => handlePrimaryShelfLifeInput(e)}/>
                                                                <select className={getFieldErrorMessageByErrorCodeStartsWith("primaryItemStackShelfLifeOption.shelfLifePresentation") ? "danger" : null}
                                                                        value={primaryShelfLifeMeasure}
                                                                        onInput={(e) => handlePrimaryShelfLifeMeasureInput(e)}
                                                                        name="primary-shelf-life-measure"
                                                                        id="primary-shelf-life-measure">
                                                                    <option value="">-</option>
                                                                    <option value="сут">сут</option>
                                                                    <option value="мес">мес</option>
                                                                    <option value="г">г</option>
                                                                </select>
                                                                {getFieldErrorMessageByErrorCodeStartsWith("primaryItemStackShelfLifeOption.shelfLifePresentation") && <p className="danger">{getFieldErrorMessageByErrorCodeStartsWith("primaryItemStackShelfLifeOption.shelfLifePresentation")}</p>}
                                                            </div>

                                                            <div className="row">
                                                                <div className="title"></div>
                                                                <a href="#" className="link" onClick={(e) => handleRemovePrimaryStorageInfo(e)}>Удалить информацию</a>
                                                            </div>
                                                        </React.Fragment>
                                                    }

                                                    <div className="text-divider">
                                                        <div className="title">После вскрытия</div>
                                                        {!afterOpeningStorageInfoAdded &&
                                                            <p>Условия хранения меняются после вскрытия упаковки? Вы можете указать это!</p>
                                                        }
                                                    </div>

                                                    {!afterOpeningStorageInfoAdded &&
                                                        <div className="row">
                                                            <div className="title"></div>
                                                            <a href="#" className="link" onClick={(e) => handleAddAfterOpeningStorageInfo(e)}>Добавить</a>
                                                        </div>
                                                    }

                                                    {afterOpeningStorageInfoAdded &&
                                                        <React.Fragment>
                                                            <div className="row">
                                                                <div className="title"><label htmlFor="after-opening-storage-mode">Условия хранения</label></div>
                                                                <textarea className={getFieldErrorMessageByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.storageMode") ? "danger" : ""}
                                                                          name="after-opening-storage-mode"
                                                                          id="after-opening-storage-mode"
                                                                          placeholder="При температуре 0°С до +6°С"
                                                                          onInput={(e) => handleAfterOpeningStorageModeInput(e)}
                                                                          value={afterOpeningStorageMode}>
                                                                </textarea>
                                                                {getFieldErrorMessageByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.storageMode") && <p className="danger">{getFieldErrorMessageByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.storageMode")}</p>}
                                                            </div>
                                                            <div className="row">
                                                                <div className="title"><label
                                                                    htmlFor="after-opening-shelf-life">Срок годности</label>
                                                                </div>
                                                                <input className={getFieldErrorMessageByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.shelfLifePresentation") ? "danger short" : "short"}
                                                                       id="after-opening-shelf-life"
                                                                       type="text"
                                                                       name="after-opening-shelf-life"
                                                                       value={afterOpeningShelfLife}
                                                                       placeholder="1"
                                                                       onInput={(e) => handleAfterOpeningShelfLifeInput(e)}/>
                                                                <select className={getFieldErrorMessageByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.shelfLifePresentation") ? "danger" : ""}
                                                                        value={afterOpeningShelfLifeMeasure}
                                                                        onInput={(e) => handleAfterOpeningShelfLifeMeasureInput(e)}
                                                                        name="after-opening-shelf-life-measure"
                                                                        id="after-opening-shelf-life-measure">
                                                                    <option value="">-</option>
                                                                    <option value="сут">сут</option>
                                                                    <option value="мес">мес</option>
                                                                    <option value="г">г</option>
                                                                </select>
                                                                {getFieldErrorMessageByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.shelfLifePresentation") && <p className="danger">{getFieldErrorMessageByErrorCodeStartsWith("afterOpeningItemStackShelfLifeOption.shelfLifePresentation")}</p>}
                                                            </div>
                                                            <div className="row">
                                                                <div className="title"></div>
                                                                <a href="#" className="link" onClick={(e) => handleRemoveAfterOpeningStorageInfo(e)}>Удалить информацию</a>
                                                            </div>
                                                        </React.Fragment>
                                                    }

                                                    <div className="row">
                                                        <div className="title"></div>
                                                        <input type="submit" className="button" value="Создать"/>
                                                    </div>
                                                </form>
                                            );

                                        }
                                    }
                                </CsrfConsumer>
                            </div>
                        );
                    }
                }
            </UserConsumer>
        </ErrorBoundary>
    );
}

export default inCage(withHeader(CreateItemStackPage))