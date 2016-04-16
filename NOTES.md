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
	
#StickyHeaderLayoutManager
 
I can't just 'new' a GhostHeader and call addView(), it has to be vended from RecyclerView or else the app terminates
I might need to take a new tack on the adapterPositioning and how the SectioningAdapter works
	---
	0: ActualHeader
	1: GhostHeader
	2: Item0
	3: Item1
	4: Footer
	---
	5: ActualHeader
	6: GhostHeader
	7: Item0
	8: Footer

SectioningAdapter would make the binding of GhostHeader automatic so users don't need to worry about it

TESTING:
	- need to ensure this works fine for adapters which don't have footer views and which don't have header views (!!!)
	- need to test against different top/bottom padding scenarios
