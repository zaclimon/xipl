package com.zaclimon.iptvproviderlibrary.ui.vod;

import android.app.Activity;
import android.os.Build;
import android.support.v17.leanback.media.PlaybackTransportControlGlue;
import android.support.v17.leanback.media.PlayerAdapter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.widget.Toast;

import static java.security.AccessController.getContext;

/**
 * Created by isaac on 17-08-11.
 */

public class ProviderVideoMediaPlayerGlue<T extends PlayerAdapter> extends PlaybackTransportControlGlue<T> {

    private PlaybackControlsRow.PictureInPictureAction mPipAction;

    public ProviderVideoMediaPlayerGlue(Activity context, T impl) {
        super(context, impl);
        mPipAction = new PlaybackControlsRow.PictureInPictureAction(context);
    }

    @Override
    protected void onCreateSecondaryActions(ArrayObjectAdapter adapter) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // TODO: Fix Picture-in-Picture "divide by zero" crash.
            //adapter.add(mPipAction);
        }
    }

    @Override
    public void onActionClicked(Action action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action);
            return;
        }
        super.onActionClicked(action);
    }

    private boolean shouldDispatchAction(Action action) {
        return action == mPipAction;
    }

    private void dispatchAction(Action action) {
        if (action == mPipAction) {
            ((Activity) getContext()).enterPictureInPictureMode();
        } else {
            Toast.makeText(getContext(), action.toString(), Toast.LENGTH_SHORT).show();
            PlaybackControlsRow.MultiAction multiAction = (PlaybackControlsRow.MultiAction) action;
            multiAction.nextIndex();
            notifyActionChanged(multiAction);
        }
    }

    private void notifyActionChanged(PlaybackControlsRow.MultiAction action) {
        int index = -1;
        if (getPrimaryActionsAdapter() != null) {
            index = getPrimaryActionsAdapter().indexOf(action);
        }
        if (index >= 0) {
            getPrimaryActionsAdapter().notifyArrayItemRangeChanged(index, 1);
        } else {
            if (getSecondaryActionsAdapter() != null) {
                index = getSecondaryActionsAdapter().indexOf(action);
                if (index >= 0) {
                    getSecondaryActionsAdapter().notifyArrayItemRangeChanged(index, 1);
                }
            }
        }
    }

    private ArrayObjectAdapter getPrimaryActionsAdapter() {
        if (getControlsRow() == null) {
            return null;
        }
        return (ArrayObjectAdapter) getControlsRow().getPrimaryActionsAdapter();
    }

    private ArrayObjectAdapter getSecondaryActionsAdapter() {
        if (getControlsRow() == null) {
            return null;
        }
        return (ArrayObjectAdapter) getControlsRow().getSecondaryActionsAdapter();
    }

}
