function ProxiedLink ({href, redirect, style="", children}) {

    async function clickHandler(e) {
        e.preventDefault();

        const response = await fetch('/api/logout');

        if (response.status === 200) {
            window.location.href = redirect;
        } else {
            alert(response.body)
        }
    }

    return (
        <a className={"link " + style} href={href} onClick={clickHandler}>
            {children}
        </a>
    );
}

export default ProxiedLink;