import React from 'react';

export default ({ node }) => {
  node.selectable=false;
  const data = node.data;
  let symbol = "-";
  if(data["_hideFlag"]){
    symbol = "+";
  }
  return (
    <div className="full-width-row">
      <div className="full-width-summary" style={{marginLeft:"20px",lineHeight:"2.5em"}}>
      { Object.keys(data).map(key => (
        <>
        {key == data["_extraDataColumnName"] &&  (
          <>
            <label>
              <b>{key} : </b>
              {data[key]}
            </label>
            <button style={{marginLeft:"20px"}} onClick={(e)=>{
              let hideFlag = false;
              node.beans.gridApi.forEachLeafNode( (node) => {
                const index = data["_indexList"].findIndex(index=>index==node.id);
                const nodeData = node.data;
                if(index>-1 && !nodeData["_hide"]){
                  hideFlag = true;
                  nodeData["_hide"] = true;
                  node.setData(nodeData);
                }else if(index>-1){
                  nodeData["_hide"] = false;
                  node.setData(nodeData);
                }
              });
              node.beans.gridApi.onFilterChanged();
              data["_hideFlag"] = hideFlag;
              node.setData(data); 
            }}>
              {symbol}
            </button>
          </>
        )}
        </>
      ))}
        <br />
      </div>
    </div>
  );
};