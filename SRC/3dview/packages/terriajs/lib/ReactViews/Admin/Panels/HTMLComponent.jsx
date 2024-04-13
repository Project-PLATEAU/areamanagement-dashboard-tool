import React, { useLayoutEffect,useState, useEffect, useRef } from "react";
export default (props) => {
    const htmlString = props.htmlString;
    const htmlRef = useRef();
    useLayoutEffect(() => {
      try{
      if (!htmlRef.current) {
        return;
      }
      (async () => {
        const scriptStrings = htmlString.match(
          /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi
        );
        
        let updatedHtmlString = htmlString;
        await scriptStrings.reduce(async (acc, current) => {
          await acc;
  
          const scriptFragment = document
            .createRange()
            .createContextualFragment(current);
          const scriptElement = scriptFragment.querySelector('script');
  
          if (scriptElement.src === '') {
            return Promise.resolve();
          }
    
          updatedHtmlString = updatedHtmlString.replace(current, '');
    
          if (
            Array.from(document.querySelectorAll('script')).some(
              se => se.src === scriptElement.src
            )
          ) {
            return Promise.resolve();
          }
      
            return new Promise(resolve => {
            scriptElement.addEventListener('load', () => {
              resolve();
            });
  
            document.head.appendChild(scriptElement);
          });
        }, Promise.resolve());
        
        const fragment = document
          .createRange()
          .createContextualFragment(updatedHtmlString);
        
        htmlRef.current.appendChild(fragment);
      })();
      }catch(e){
        console.log(e);
      }
    }, [htmlString]);  
    return <html ref={htmlRef} />;
  };