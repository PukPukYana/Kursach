import React from "react";

const UserContext = React.createContext({
    user: null,
    isLoaded: false,
    hasError: false
});

const UserProvider = UserContext.Provider;
const UserConsumer = UserContext.Consumer;

export {UserProvider, UserConsumer};