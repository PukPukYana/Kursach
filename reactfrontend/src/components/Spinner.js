import "./css/Spinner.css";

function Spinner ({active=false, height = 20, width = 20}) {

    if(!active) {
        return <span id="spinner-placeholder"></span>;
    }

    return (
        <svg id="spinner"
             className="spinner"
             width={width + "px"}
             height={height + "px"}
             viewBox="0 0 66 66"
             xmlns="http://www.w3.org/2000/svg">
            <circle className="path" fill="none" strokeWidth="6" strokeLinecap="round" cx="33" cy="33"
                    r="30"></circle>
        </svg>
    );
}

export default Spinner;