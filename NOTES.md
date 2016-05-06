#BUGS

	SectioningAdapter::notifySectionRemoved is buggy
	 - to reproduce, load StickyHeaderCallbacks, scroll so section 2 is near top and section 3 is just visible and click X
	 onLayoutChildren is called, and header for section 3 is NOT repositioned.
	 if you hit the refresh icon, onLayoutChildren is called AGAIN, and the header is correctly positioned
	 some state is different between the two calls
	 
	 SectioningAdapter::notifySectionItemRemoved is also buggy
	 
	 The problem clearly lies with StickyHeaderLayoutManager, not with SectioningAdapter, since using SectioningAdapter with a LinearLayoutManager works fine for both above operations.
	 
	 
	 Am attempting a rewrite of onLayoutChildren where I don't query the adapter
	    it mostly works, but headers which are not the topmost are placed in sticky position...
	 

#TESTING:
	- need to ensure this works fine for adapters which don't have footer views and which don't have header views (!!!)
	- need to test against different top/bottom padding scenarios


#RecyclerView.LayoutManager

Here's my understanding of how a linear layout manager works
- When RV has a change to the number of items it represents, onLayoutChildren is called, and in there, a number of views are vended and positioned (precisely)
	- note that in the demo code log call, top is a sane number like -163 representing the top of the first item. items are stacked according to height until they fall off the bottom.
	
- When user scrolls, scrollVerticallyBy is called
	- items are vertically shifted
	- new spaces are filled via same item vending process as onLayoutChildren
	- out of bounds items are recycled
	
firstPosition seems to be the position in the logical list of the first item on screen - it's used to know which item to vend from the adapter
	- firstPosition is updated on scroll and in recyling offscreen views

LayoutManager::getChildCount() returns number of views attached to RV, not number of hypothetical views in list
