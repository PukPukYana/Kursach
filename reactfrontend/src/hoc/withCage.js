import React from "react";

function inCage(WrappedPage) {

    function CagedPage () {
        return (
            <div className="cage">
                <WrappedPage/>
            </div>
        );
    }

    return CagedPage;
}

export default inCage;