import java.util.*;

public class Calculate2NFDecomposition extends CalculateDecomposition {

    Calculate2NFDecomposition(Relation inputRelation) {
        super(inputRelation);
        decompose();
    }
    @Override
    protected void decompose() {
        if (getInputRelation().getInputFDs().isEmpty()) {
			// setOutputMsgFlag(true);
			// setOutputMsg("No functional dependencies provided in input relation, therefore input relation is already in BCNF.");
			return;
        }
        if (getInputRelation().getNormalFormsResults().isIn2NF()) {
			setOutputMsgFlag(true);
			//setOutputMsg("Input relation is already in BCNF. No decomposition necessary. ");
			return;
        }
        List<Relation> result = decompose2NFHelper(getInputRelation());
       /* List<FunctionalDependency> lostFDs = findEliminatedFunctionalDependencies(result , RDTUtils.getSingleAttributeMinimalCoverList(getInputRelation().getMinimalCover(), getInputRelation()));
        for(FunctionalDependency fd : lostFDs) {
            List<Attribute> decomposedAttrs = new ArrayList<>();
            int counter = result.size();
			decomposedAttrs.addAll(fd.getLeftHandAttributes());
            decomposedAttrs.addAll(fd.getRightHandAttributes());
            List<FunctionalDependency> decomposedFD = RDTUtils.fetchFDsOfDecomposedR(RDTUtils.getSingleAttributeMinimalCoverList(getInputRelation().getMinimalCover(), getInputRelation()),
                    decomposedAttrs);
            Relation twoNFRelation = new Relation(getInputRelation().getName() + counter++, decomposedAttrs, decomposedFD);
            result.add(twoNFRelation);        
        }
        result = eliminateDuplicateSubsetRelations(result);*/
        for(Relation r: result) {
            addRelationtoOutputList(r);
        }
    }

    public List<Relation> decompose2NFHelper(Relation r) {
        List<Relation> result = new ArrayList<>();
        int counter = 0;
		if (r.getClosures().isEmpty()) {
			CalculateClosure.improvedCalculateClosures(r);
		}
		if (r.getMinimalCover().isEmpty()) {
			MinimalFDCover.determineMinimalCover(r);
		}
		if (r.getMinimumKeyClosures().isEmpty()) {
			CalculateKeys.calculateKeys(r);
		}
		if (!r.getNormalFormsResults().hasDeterminedNormalForms) {
			r.determineNormalForms();
        }
        if (r.getNormalFormsResults().isIn2NF()) {
			result.add(r);
			return result;
        }
        //CalculateFDs.calculateDerivedFDs(r);
        //System.out.println("In 2nf");
        for(Closure c: r.getNormalFormsResults().getTwoNFViolatingFDs()){
            Closure leftSideClosure = c;
            //System.out.println(c.printCompleteClosure());
            List<FunctionalDependency> r1FDs = RDTUtils.fetchFDsOfDecomposedR(RDTUtils.getSingleAttributeMinimalCoverList(r.getInputFDs(), r), leftSideClosure.getClosure());
            Relation r1 = new Relation(r.getName() + "_" + counter++, leftSideClosure.getClosure(), r1FDs);
            List<Attribute> r2Attributes = new ArrayList<>();
            for (Attribute a : c.getClosureOf()) {
				if (!RDTUtils.attributeListContainsAttribute(r2Attributes, a)) {
					r2Attributes.add(a);
				}
            }
            for (Attribute a : r.getAttributes()) {
				if (!RDTUtils.attributeListContainsAttribute(leftSideClosure.getClosure(), a)) {
					if (!RDTUtils.attributeListContainsAttribute(r2Attributes, a)) {
						r2Attributes.add(a);
					}
				}
            }
            List<FunctionalDependency> r2FDs = RDTUtils.fetchFDsOfDecomposedR(RDTUtils.getSingleAttributeMinimalCoverList(r.getInputFDs(), r), r2Attributes);
            Relation r2 = new Relation(r.getName() + "_" + counter++, r2Attributes, r2FDs);
            result.addAll(decompose2NFHelper(r1));
            result.addAll(decompose2NFHelper(r2));
        }
        List<Relation> withoutDuplicates = eliminateDuplicateSubsetRelations(result);
        return withoutDuplicates;
    }

    private List<Relation> eliminateDuplicateSubsetRelations(final List<Relation> workingOutputRelations) {
		List<Relation> output = new ArrayList<>();
		boolean[] removeIndices = new boolean[workingOutputRelations.size()];
		for (int i = 0; i < removeIndices.length; i++) {
			removeIndices[i] = false;
		}
		for (int i = 0; i < workingOutputRelations.size(); i++) {
			if (!removeIndices[i]) {
				Relation currentRelation = workingOutputRelations.get(i);
				for (int j = 0; j < workingOutputRelations.size(); j++) {
					if (i != j && !removeIndices[j]) {
						Relation otherRelation = workingOutputRelations.get(j);
						if (RDTUtils.isAttributeListSubsetOfOtherAttributeList(currentRelation.getAttributes(),
								otherRelation.getAttributes())) {
							removeIndices[j] = true;
						}
					}
				}
			}
		}
		for (int i = 0; i < workingOutputRelations.size(); i++) {
			if (!removeIndices[i]) {
				output.add(workingOutputRelations.get(i));
			}
		}
		return output;
	}
    
    private List<FunctionalDependency> findEliminatedFunctionalDependencies(final List<Relation> outputRelations, final List<FunctionalDependency> inputFDs) {
		if (outputRelations == null || inputFDs == null) {
			throw new IllegalArgumentException("Input list of relations or input list of functional dependencies is null.");
		}
		List<FunctionalDependency> missingFDs = new ArrayList<>();
		for (FunctionalDependency originalFD : inputFDs) {
			boolean found = false;
			for (Relation bcnfR : outputRelations) {
				if (RDTUtils.isFunctionalDependencyAlreadyInFDList(originalFD, bcnfR.getInputFDs())) {
					found = true;
					break;
				}
			}
			if (!found) {
				missingFDs.add(originalFD);
			}
		}
		return missingFDs;
	}
}
