'use strict';
import React from 'react';
import Icon, { StyledIcon } from "../../../../Styled/Icon";
import Spacing from "../../../../Styled/Spacing";
import Text from "../../../../Styled/Text";
import Box from "../../../../Styled/Box";
import Input from "../../../../Styled/Input";
import Button, { RawButton } from "../../../../Styled/Button";
import Style1 from "./style.scss";


export default function Content(props) {
    let columns = props.columnDefs;
    let data = props.rowData;

    return (
        <>
            <table className={Style1.themeLayerTable2} style={{width:"100%", borderSpacing: "0px"}}>
                <thead className>
                    <tr style={{background:"#f2f2f2",color:"#666",position:"sticky",top:"0",left:"0",zIndex:"999"}}>
                        {columns.map((item) =>
                            <th style={{width: item.width, height:"40"}}>{item.headerName}</th>
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
                        `}
                        height="40">
                            {columns.map((value2) =>
                                <td style={{overflowX:"auto"}} css={`
                                ${value2.textAlign &&
                                `text-align:`+value2.textAlign}
                                `}>
                                    {value2.customComponent&& (
                                        (value2.customComponent(index))
                                    )}
                                    {!value2.customComponent && value2.field != "dispOrder" && (
                                        value1[value2.field] || value1["layerForm"]?.[value2.field]
                                    )}
                                    {!value2.customComponent && value2.field == "dispOrder" && (
                                        <Input
                                            light={true}
                                            dark={false}
                                            type="number"
                                            className={"dispNumForThemeLayer"}
                                            id={"dispOrder" + index}
                                            min="0"
                                            defaultValue={value1[value2.field] || value1["layerForm"]?.[value2.field]}
                                        />
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