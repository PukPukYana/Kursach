import "./css/ProfilePageRow.css";

function ProfilePageRow ({title, value, heading=false, href=null, valueColor = "inherit"}) {

    return (
        <div className={"profile-row " + (heading ? "heading-row" : "")}>

            {
                title ? <div className="row-title">{title}</div> : null
            }
            <div className="row-value" style={{color: valueColor}}>
                {
                    href ? <a className="link" href={href}>{value}</a> : value
                }
            </div>

        </div>
    );
}

export default ProfilePageRow;