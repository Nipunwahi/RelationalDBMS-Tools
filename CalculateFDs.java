
import java.util.ArrayList;
import java.util.List;

public class CalculateFDs {

	public static void calculateDerivedFDs(final Relation relation) {
		if (relation.getClosures().isEmpty()) {
			CalculateClosure.improvedCalculateClosures(relation);
		}
		for (Closure c : relation.getClosures()) {
			List<Attribute> rightSide = new ArrayList<>();
			for (Attribute a : c.getClosure()) {
				if (!RDTUtils.attributeListContainsAttribute(c.getClosureOf(), a)) {
					rightSide.add(a);
				}
			}
			if (!rightSide.isEmpty()) {
				FunctionalDependency derived = new FunctionalDependency(c.getClosureOf(), rightSide, relation);
				relation.addDerivedFunctionalDependency(derived);
			}
		}
		relation.sortFDs();
		return;
	}
}
