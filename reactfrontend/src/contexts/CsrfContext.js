import React from "react";

const CsrfContext = React.createContext({
    csrfToken: null,
    csrfHeaderName: null,
    isLoaded: false
});

const CsrfProvider = CsrfContext.Provider;
const CsrfConsumer = CsrfContext.Consumer;

export {CsrfProvider, CsrfConsumer};