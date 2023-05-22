import {UserConsumer} from "../contexts/UserContext";

function HeaderMenuItem({url, name, needsUser = true}) {
    return (
        <UserConsumer>
            {
                (userContext) => {
                    return (!needsUser || userContext.user) && (
                        <li className="header-menu-item"><a className="menu-item-link" href={url}>{name}</a></li>
                    );
                }
            }
        </UserConsumer>
    );
}

export default HeaderMenuItem;