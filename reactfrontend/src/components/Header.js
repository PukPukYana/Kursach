import './css/Header.css';
import {useState} from "react";
import HeaderMenuItem from "./HeaderMenuItem";

function Header() {

    const [menuItems, setMenuItems] = useState([
        {
            id: 1,
            url: "/profile",
            name: "Профиль",
            needsUser: true
        },
        {
            id: 2,
            url: "/archive",
            name: "Архив",
            needsUser: true
        }
    ]);

    return (
        <header className="header">

            <div className="title-container"><a className="title" href="/">SberManager</a></div>

            <ul className="menu-container">
                {menuItems.map(item => {
                    return (
                        <HeaderMenuItem key={item.id}
                                        url={item.url}
                                        name={item.name}
                                        needsUser={item.needsUser}/>
                    );
                })}
            </ul>


        </header>
    );
}

export default Header;