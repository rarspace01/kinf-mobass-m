package edu.denishamann.guesstimate;

import java.util.ArrayList;
import java.util.Iterator;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

public class OsmItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mItemList = new ArrayList<OverlayItem>();

	public OsmItemizedOverlay(ArrayList<OverlayItem> pList, ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener, ResourceProxy pResourceProxy) {
		super(pList, pOnItemGestureListener, pResourceProxy);
		mItemList = pList;
	}

	public void addOverlay(OverlayItem aOverlayItem) {
		mItemList.add(aOverlayItem);
		populate();
	}

	public void removeOverlay(OverlayItem aOverlayItem) {
		mItemList.remove(aOverlayItem);
		populate();
	}

	public void removeOverlayByTitle(String title) {
		Iterator<OverlayItem> iterator = mItemList.iterator();
		while (iterator.hasNext()) {
			OverlayItem curItem = iterator.next();
			if (curItem.mTitle.equals(title)) {
				iterator.remove();
				break;
			}
		}
		populate();
	}
}
