import React from "react";
import Header from "../components/Header";

function withHeader(WrappedPage) {

    function PageWithHeader () {
        return (
            <React.Fragment>
                <Header/>
                <WrappedPage/>
            </React.Fragment>
        );
    }

    return PageWithHeader;
}

export default withHeader;