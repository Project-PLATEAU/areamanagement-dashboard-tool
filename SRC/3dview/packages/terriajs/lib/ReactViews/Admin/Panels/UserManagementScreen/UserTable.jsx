'use strict';
import React from 'react';
import Styles from "./user-edit-panel.scss";


export default function Content(props) {
    let columns = props.columnDefs;
    let data = props.rowData;

    return (
        <>
            <table className={Styles.themeLayerTable} style={{width:"100%", borderSpacing: "0px"}}>
                <thead>
                    <tr style={{background:"#f2f2f2",color:"#666",position:"sticky",top:"0",left:"0",zIndex:"999"}}>
                        {columns.map((item) =>
                            <th style={{width: item.width}} css={'text-align:left; padding:0.5em; padding-left:2em;'}>{item.headerName}</th>
                        )}
                    </tr>
                </thead>
                <tbody style={{width:"100%"}}>
                    {data.map((value1,index) =>
                        <tr css={`
                        ${index%2 == 0 &&
                        `background: #fff`}
                        ${index%2 == 1 &&
                        `background: #FAFAFA`}
                        `}
                       >
                            {columns.map((value2) =>
                                <td style={{overflowX:"auto"}} css={'padding:0.5em; padding-left:2em;'}>
                                    {value2["field"] != "role" && (
                                        <>{value1[value2.field]}</>
                                    )}
                                    {value2["field"] == "role" && (
                                        <>
                                        {value1["role"] == "admin" && (<>管理ユーザ</>)}
                                        {value1["role"] == "user" && (<>地域住民ユーザ</>)}
                                        {value1["role"] == "erimane" && (<>エリマネ団体ユーザ</>)}
                                        </>
                                    )}
                                    {value2.customComponent &&(
                                        (value2.customComponent(index))
                                    )}
                                </td>
                            )}
                        </tr>
                    )}
                </tbody>
            </table>
        </>
    );
}