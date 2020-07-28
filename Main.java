import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		//String attributes = "R(NAME, STREET, CITY, TITLE, YEAR)";
		//String functionalDependencies = "";
		//String multivaluedDependencies = "NAME->STREET,CITY";
        // String attributes = "R(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S)";
        // String functionalDependencies = "TITLE, YEAR -> STUDIONAME; STUDIONAME -> PRESIDENT; PRESIDENT -> PRESADDR";
        // String multivaluedDependencies = "";
		System.out.println("Please enter input in the following format: ");
		System.out.println("Relation as R(A,B,C,D)	fds as A->B,C;C->D");
		String attributes;
		String functionalDependencies;
		String multivaluedDependencies="";
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the relation: ");
		attributes=in.nextLine();
		System.out.println("Enter the functional dependencies: ");
		functionalDependencies=in.nextLine();
		Relation relation = new Relation(attributes);
		relation.addFunctionalDependencies(functionalDependencies);
		relation.addMultivaluedDependencies(multivaluedDependencies);
		if (!relation.hasPassedIntegrityChecks()) {
			System.out.println("Failed integrity check.");
			System.out.println(relation.getIntegrityCheckErrorMsg());
		}
		System.out.println("Input relation: " + relation.printRelation());
		System.out.println("-------------");
        CalculateClosure.improvedCalculateClosures(relation);
        // CalculateFDs.calculateDerivedFDs(relation);
        // System.out.println("Derived Fd");
        // for(FunctionalDependency fd : relation.getDerivedFDs()){
        //     System.out.println(fd.getFDName());
        // }
		// for (Closure c : relation.getClosures()) {
		// 	System.out.println(c.printCompleteClosure());
		// }
		System.out.println("-------------");
		System.out.println("Minimum key closures: ");
		for (Closure c : relation.getMinimumKeyClosures()) {
			System.out.println(c.printLeftSideAttributes());
		}
		// System.out.println("Superkey closures: ");
		// for (Closure c : relation.getSuperKeyClosures()) {
		// 	System.out.println(c.printLeftSideAttributes());
		// }
		System.out.println("-------------");
		MinimalFDCover.determineMinimalCover(relation);
		System.out.println("Minimal cover: ");
		for (FunctionalDependency fd : relation.getMinimalCover()) {
			System.out.println(fd.getFDName());
		}
		System.out.println("-------------");
		relation.determineNormalForms();
		DetermineNormalForms normalForms = relation.getNormalFormsResults();
		//System.out.println(normalForms.getSecondNormalFormMsg());
		//System.out.println(normalForms.getThirdNormalFormMsg());
		//System.out.println(normalForms.getBCNFMsg());
        //	System.out.println(normalForms.getFourthNormalFormMsg());
        System.out.println("\n\n2NF");
        Calculate2NFDecomposition TwoNF = new Calculate2NFDecomposition(relation);
        if(normalForms.isIn2NF()) {
            System.out.println("Already in 2NF");
        } else {
            List<Relation> Twonfoutput = TwoNF.getOutputRelations();
            for(Relation r: Twonfoutput) {
                System.out.println(r.printRelation());
                System.out.println("KEYS");
                for (Closure c : r.getMinimumKeyClosures()) {
                    System.out.println(c.printLeftSideAttributes());
                }
            }
        }
        System.out.println("\n\n3NF");
		Calculate3NFDecomposition threeNF = new Calculate3NFDecomposition(relation);
		if (normalForms.isIn3NF()) {
			System.out.println("Already in 3NF");
		} else {
			threeNF.decompose();
			List<Relation> output3NFRelations = threeNF.getOutputRelations();
			for (Relation r : output3NFRelations) {
                System.out.println(r.printRelation());
                System.out.println("KEYS");
                if (r.getClosures().isEmpty()) {
                    CalculateClosure.improvedCalculateClosures(r);
                }
                if (r.getMinimalCover().isEmpty()) {
                    MinimalFDCover.determineMinimalCover(r);
                }
                if (r.getMinimumKeyClosures().isEmpty()) {
                    CalculateKeys.calculateKeys(r);
                }
                for (Closure c : r.getMinimumKeyClosures()) {
                    System.out.println(c.printLeftSideAttributes());
                }
			}
        }

        System.out.println("\n\nBCNF");
    CalculateBCNFDecomposition bcnf = new CalculateBCNFDecomposition(threeNF);
    //System.out.println("*********************");
    // threeNF.force3NFDecomposition();
    // System.out.println("3NF");
    // List<Relation> outputThreenf = threeNF.getOutputRelations();
    // for(Relation r : outputThreenf) {
    //   System.out.println(r.printRelation());
    //   r.determineNormalForms();
    //   normalForms = r.getNormalFormsResults();
	//   	//System.out.println(normalForms.getSecondNormalFormMsg());
	// 	  //System.out.println(normalForms.getThirdNormalFormMsg());
	// 	  //System.out.println(normalForms.getBCNFMsg());
    // }

		//System.out.println(bcnf.getOutputMsg());
		//System.out.println("Results with possible duplicates: ");
		// for (Relation r : bcnf.getResultWithDuplicates()) {
		// 	System.out.println(r.printRelation());
		// }
    //System.out.println("Final results:");
    bcnf.decompose();
    if(normalForms.isInBCNF()) {
        System.out.println("Already in BCNF");
    }
    else {
        if(bcnf != null) {
            List<Relation> outputBCNFRelations = bcnf.getPureBCNFDecomposedRs();
            if(outputBCNFRelations != null) {
                for (Relation r : outputBCNFRelations) {
                    //CalculateClosure.improvedCalculateClosures(r);
                    System.out.println(r.printRelationMinimalKey());
                    System.out.println("KEYS");
                    for (Closure c : r.getMinimumKeyClosures()) {
                        System.out.println(c.printLeftSideAttributes());
                    }
            }
          }
        }
        // List<FunctionalDependency> pureBCNFLostFDs = bcnf.getPureBCNFLostFDs();
        // if (pureBCNFLostFDs.isEmpty()) {
        //     System.out.println("No FDs Lost");
        // } else {
        //     System.out.println("Lost Fds");
        //     for (int i = 0; i < pureBCNFLostFDs.size(); i++) {
        //         System.out.println(pureBCNFLostFDs.get(i).getFDName());
        //     }
        //     // appendOutput(".", false);
        //     // appendOutput("Note that a lost input functional dependency can be safely ignored if it is not part of the minimal cover set of functional dependencies", true);
        // }
    }

}

}
