import React from 'react';

export default (params) => {
    return (
    <a href={params.value} target={"_blank"} rel={"noopener"}>リンク先を表示</a>
    )
};