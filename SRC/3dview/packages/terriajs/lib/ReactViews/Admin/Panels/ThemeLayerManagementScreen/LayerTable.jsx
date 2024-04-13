'use strict';
import React from 'react';
import Style1 from "./style.scss";


export default function Content(props) {
    let columns = props.columnDefs;
    let data = props.rowData;
    return (
        <>
            <table className={Style1.themeLayerTable1} style={{width:"100%", borderSpacing: "0px",}}>
                <thead className>
                    <tr style={{background:"#f2f2f2",color:"#999",position:"sticky",top:"0",left:"0",zIndex:"999"}}>
                        {columns.map((item) =>
                            <th style={{width: item.width}}>{item.headerName}</th>
                        )}
                    </tr>
                </thead>
                <tbody>
                    {data.length > 0 && (
                        data.map((value1,index) =>
                        <tr css={`
                        ${index%2 == 0 &&
                        `background: #fff`}
                        ${index%2 == 1 &&
                        `background: #FAFAFA`}
                        `}>
                            {columns.map((value2) =>
                                <td style={{overflowX:"auto"}} css={`
                                ${value2.textAlign &&
                                `text-align:`+value2.textAlign}
                                `}>
                                    {value2["field"] == "publishFlag" && (
                                        (value2.customComponent(index))
                                    )}
                                    {value2["field"] == "button" && value1["layerType"] == 1  && (
                                        (value2.customComponent(index))
                                    )}
                                    {!value2.customComponent && (
                                        value1[value2.field] || value1["layerForm"]?.[value2.field]
                                    )}
                                </td>
                            )}
                        </tr>
                    ))}
                </tbody>
            </table>
        </>
    );
}