import "./css/UniversalMessageWithLink.css";

function UniversalMessageWithLink({message, linkHref, linkText}) {

    return (
        <div className="universal-message-with-link">
            <p>{message}</p>
            <a href={linkHref} className="link">{linkText}</a>
        </div>
    );
}

export default UniversalMessageWithLink;