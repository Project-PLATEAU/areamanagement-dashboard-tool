'use strict';
import React from 'react';
import { action, runInAction } from "mobx";
import Style from "./common.scss";


export default function Content(props) {
    let columns = props.columns;
    let data = props.data;

    return (
        <>
            <table className={Style.z_table} style={{width:"100%"}}>
                <thead className="table-dark">
                    <tr style={{background:"##F2F2F2",color:"#fff",position:"sticky",top:"0",left:"0",zIndex:"10"}}>
                        {columns.map((value) =>
                            <th width={value.width} css={`
                            ${value.textAlign &&
                            `text-align:`+value.textAlign}
                            ${value.fontSize &&
                            `font-size:`+value.fontSize+`;`}
                             white-space: nowrap;
                             padding-left: 1em;
                             padding-right: 1em;
                            `}>{value.Header}</th>
                        )}
                    </tr>
                </thead>
                <tbody>
                    {data.map((value1,index) =>
                        <>
                        {!value1.clickEventFlag && (
                        <tr css={`
                        ${index%2 == 0 &&
                        `background:#fff;`}
                        ${index%2 == 1 &&
                        `background:#FAFAFA;`}
                        `}
                        height="50">
                            {columns.map((value2) =>
                                <td width={value1[value2.accessor].width} height="50" style={{overflowX:"auto"}} css={`
                                ${value1[value2.accessor].textAlign &&
                                `text-align:`+value1[value2.accessor].textAlign}
                                ${value1[value2.accessor].fontSize &&
                                `font-size:`+value1[value2.accessor].fontSize+`;`}
                                `}>
                                    {(!value1[value2.accessor].customComponent || !value1[value2.accessor].props) && !isValidUrl(value1[value2.accessor].value) && (
                                        <span style={{minWidth:"170px",display:"block"}}>{value1[value2.accessor].value}</span>
                                    )}
                                    {(!value1[value2.accessor].customComponent || !value1[value2.accessor].props) && isValidUrl(value1[value2.accessor].value) && (
                                        <span style={{minWidth:"100px",display:"block"}}><a href={value1[value2.accessor].value} target='blank'>添付ファイル</a></span>
                                    )}
                                    {(value1[value2.accessor].customComponent && value1[value2.accessor].props) && (value1[value2.accessor].customComponent(value1[value2.accessor].props))}
                                </td>
                            )}
                        </tr>
                        )}
                        {value1.clickEventFlag && (
                            <tr css={`
                            ${index%2 == 0 &&
                            `background:#dcdcdc;`}
                            ${index%2 == 1 &&
                            `background:#f5f5f5;`}
                            cursor:pointer;
                            `}
                            className={Style.customHoverRow}
                            height="50">
                                {columns.map((value2) =>
                                    <td width={value1[value2.accessor].width} height="50" style={{overflowX:"auto"}} css={`
                                    ${value1[value2.accessor].textAlign &&
                                    `text-align:`+value1[value2.accessor].textAlign+`;`}
                                    ${value1[value2.accessor].fontSize &&
                                    `font-size:`+value1[value2.accessor].fontSize+`;`}
                                    `}
                                    onClick={e=>{
                                        if(!value1[value2.accessor].customComponent || !value1[value2.accessor].props){
                                            try{
                                                value1.onClick(props.this);
                                            }catch(e){}
                                        }
                                    }}
                                    >
                                        {(!value1[value2.accessor].customComponent || !value1[value2.accessor].props) && !isValidUrl(value1[value2.accessor].value) && (
                                            <span style={{minWidth:"170px",display:"block"}}>{value1[value2.accessor].value}</span>
                                        )}
                                        {(!value1[value2.accessor].customComponent || !value1[value2.accessor].props) && isValidUrl(value1[value2.accessor].value) && (
                                            <span style={{minWidth:"100px",display:"block"}}><a href={value1[value2.accessor].value} target='blank'>添付ファイル</a></span>
                                        )}
                                        {(value1[value2.accessor].customComponent && value1[value2.accessor].props) && (value1[value2.accessor].customComponent(value1[value2.accessor].props))}
                                    </td>
                                )}
                            </tr>
                        )}
                        </>
                    )}
                </tbody>
            </table>
        </>
    );
}

function isValidUrl(string) {
    try {
        const pattern = new RegExp( '^([a-zA-Z]+:\\/\\/)?' + 
        '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|' + 
        '((\\d{1,3}\\.){3}\\d{1,3}))' + 
        '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + 
        '(\\?[;&a-z\\d%_.~+=-]*)?' + 
        '(\\#[-a-z\\d_]*)?', 
        'i' ); 
        let result = pattern.test(string);
        if(!result){
            //投稿レイヤの添付ファイル画像相対パス
            let baseUrl = location.href;
            new URL(string , baseUrl);
            result = string.startsWith('/api/layers/attachments/feature_');
        }
        if(!result){
            //エリマネ・イベント活動の添付ファイル画像相対パス
            let baseUrl = location.href;
            new URL(string , baseUrl);
            result = string.startsWith('/api/activity/attachments/');
        }
        return result;
    } catch (err) { 
        return false; 
    }
}