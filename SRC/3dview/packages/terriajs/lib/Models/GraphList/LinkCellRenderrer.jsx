import React from 'react';

export default (params) => {
  if(params && params.value && params.data?._linkColumnNameList && params.data._linkColumnNameList[params.colDef.field]){
    const linkNameColumn = params.data._linkColumnNameList[params.colDef.field];
    return (
      <a href={params.value} target={"_blank"} rel={"noopener"}>{params.data[linkNameColumn]}</a>
    )
  }else if(params && params.value){
    return <>{params.value}</>;
  }else{
    return "";
  }
};