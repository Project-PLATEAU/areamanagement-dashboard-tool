import _ from "underscore";

import { open } from "metabase/lib/dom";

export function performAction(action, { dispatch, onChangeCardAndRun }) {
  let didPerform = false;
  if (action.action) {
    const reduxAction = action.action();
    if (reduxAction) {
      dispatch(reduxAction);
      didPerform = true;
    }
  }
  if (action.url) {
    const url = action.url();
    const ignoreSiteUrl = action.ignoreSiteUrl;
    if (url) {
      open(url, { ignoreSiteUrl });
      didPerform = true;
    }
  }
  if (action.question) {
    const question = action.question();
    const extra = action?.extra?.() ?? {};
    if (question) {
      onChangeCardAndRun({ nextCard: question.card(), ...extra });
      didPerform = true;
    }
  }
  return didPerform;
}

export function performDefaultAction(actions, props) {
  if (!actions) {
    return false;
  }

  // "default" action if there's only one
  if (actions.length === 1 && actions[0].default) {
    return performAction(actions[0], props);
  }

  // "defaultAlways" action even if there's more than one
  const action = _.find(actions, action => action.defaultAlways === true);
  if (action) {
    return performAction(action, props);
  }

  return false;
}