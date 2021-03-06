package org.openmrs.module.ugandaemrreports.library;

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.ugandaemrreports.UgandaEMRReportUtil;
import org.openmrs.module.ugandaemrreports.metadata.HIVMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * Common dimensions shared across multiple reports
 */
@Component
public class CommonDimensionLibrary extends BaseDefinitionLibrary<CohortDefinitionDimension> {

    @Autowired
    private CommonCohortDefinitionLibrary cohortDefinitionLibrary;
    @Autowired
    private HIVCohortDefinitionLibrary hivCohortDefinitionLibrary;

    @Autowired
    private DataFactory df;
    @Autowired
    private HIVMetadata hivMetadata;


    @Override
    public Class<? super CohortDefinitionDimension> getDefinitionType() {
        return CohortDefinitionDimension.class;
    }

    @Override
    public String getKeyPrefix() {
        return "ugemr.dim.common.";
    }

    /**
     * Gender dimension
     *
     * @return the dimension
     */
    public CohortDefinitionDimension genders() {
        CohortDefinitionDimension dimGender = new CohortDefinitionDimension();
        dimGender.setName("Gender");
        dimGender.addCohortDefinition(cohortDefinitionLibrary.males().getName(), Mapped.mapStraightThrough(cohortDefinitionLibrary.males()));
        dimGender.addCohortDefinition(cohortDefinitionLibrary.females().getName(), Mapped.mapStraightThrough(cohortDefinitionLibrary.females()));

        return dimGender;
    }

    /**
     * Dimension of age using the standard age groups
     *
     * @return the dimension
     */
    public CohortDefinitionDimension get106aAgeGroup() {
        CohortDefinitionDimension dimAges = new CohortDefinitionDimension();
        dimAges.setName("Age - 106a 1A");
        dimAges.setDescription("Age desegregation for HMIS 106A Section 1A which are  - AgesGroup (< 2 years, 2 - 5 years, 5 - 14 years, 15+ years)");
        dimAges.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
        dimAges.addCohortDefinition("< 2 years", UgandaEMRReportUtil
                .map(cohortDefinitionLibrary.below2Years(), "effectiveDate=${effectiveDate}"));
        dimAges.addCohortDefinition("2 - 5 years", UgandaEMRReportUtil
                .map(cohortDefinitionLibrary.between2And5Years(), "effectiveDate=${effectiveDate}"));
        dimAges.addCohortDefinition("5 - 14 years", UgandaEMRReportUtil
                .map(cohortDefinitionLibrary.between5And14Years(), "effectiveDate=${effectiveDate}"));
        dimAges.addCohortDefinition("15+ years", UgandaEMRReportUtil
                .map(cohortDefinitionLibrary.above15Years(), "effectiveDate=${effectiveDate}"));

        return dimAges;
    }


    /**
     * Dimension of age using the standard age and gender groups
     *
     * @return the dimension
     */
    public CohortDefinitionDimension get106aAgeGenderGroup() {
        CohortDefinitionDimension ageGenderDimension = new CohortDefinitionDimension();

        CohortDefinition below2Years = cohortDefinitionLibrary.below2Years();
        CohortDefinition between2And4Years = cohortDefinitionLibrary.between2And5Years();
        CohortDefinition between5And14Years = cohortDefinitionLibrary.between5And14Years();
        CohortDefinition above15Years = cohortDefinitionLibrary.above15Years();

        CohortDefinition males = cohortDefinitionLibrary.males();
        CohortDefinition females = cohortDefinitionLibrary.females();


        CohortDefinition a = df.getPatientsInAll(below2Years, males);
        CohortDefinition b = df.getPatientsInAll(below2Years, females);
        CohortDefinition c = df.getPatientsInAll(between2And4Years, males);
        CohortDefinition d = df.getPatientsInAll(between2And4Years, females);

        CohortDefinition e = df.getPatientsInAll(between5And14Years, males);
        CohortDefinition f = df.getPatientsInAll(between5And14Years, females);
        CohortDefinition g = df.getPatientsInAll(above15Years, males);
        CohortDefinition h = df.getPatientsInAll(above15Years, females);

        ageGenderDimension.addParameter(ReportingConstants.END_DATE_PARAMETER);
        ageGenderDimension.addCohortDefinition("below2male", Mapped.mapStraightThrough(a));
        ageGenderDimension.addCohortDefinition("below2female", Mapped.mapStraightThrough(b));
        ageGenderDimension.addCohortDefinition("between2and5male", Mapped.mapStraightThrough(c));
        ageGenderDimension.addCohortDefinition("between2and5female", Mapped.mapStraightThrough(d));
        ageGenderDimension.addCohortDefinition("between5and14male", Mapped.mapStraightThrough(e));
        ageGenderDimension.addCohortDefinition("between5and14female", Mapped.mapStraightThrough(f));
        ageGenderDimension.addCohortDefinition("above15male", Mapped.mapStraightThrough(g));
        ageGenderDimension.addCohortDefinition("above15female", Mapped.mapStraightThrough(h));
        ageGenderDimension.addCohortDefinition("child", Mapped.mapStraightThrough(cohortDefinitionLibrary.agedBetween(0, 14)));
        ageGenderDimension.addCohortDefinition("adult", Mapped.mapStraightThrough(cohortDefinitionLibrary.agedAtLeast(15)));
        return ageGenderDimension;
    }

    public CohortDefinitionDimension getTxCurrentAgeGenderGroup() {
        CohortDefinitionDimension ageGenderDimension = new CohortDefinitionDimension();

        CohortDefinition below1Year = cohortDefinitionLibrary.below1Year();
        CohortDefinition between1And4Years = cohortDefinitionLibrary.between1And4years();
        CohortDefinition between5And9Years = cohortDefinitionLibrary.between5And9years();
        CohortDefinition between10And14Years = cohortDefinitionLibrary.between10And14years();
        CohortDefinition between15And19Years = cohortDefinitionLibrary.between15And19years();
        CohortDefinition between20And24Years = cohortDefinitionLibrary.between20And24years();
        CohortDefinition between25And29Years =cohortDefinitionLibrary.between25And29years();
        CohortDefinition between30And34Years = cohortDefinitionLibrary.between30And34years();
        CohortDefinition between35And39Years = cohortDefinitionLibrary.between35And39years();
        CohortDefinition between40And49Years = cohortDefinitionLibrary.between40And49years();
        CohortDefinition above50Years = cohortDefinitionLibrary.above50Years();

        //106a age disagregates
        CohortDefinition below2Years = cohortDefinitionLibrary.below2Years();
        CohortDefinition between2And4Years = cohortDefinitionLibrary.between2And5Years();
        CohortDefinition between5And14Years = cohortDefinitionLibrary.between5And14Years();
        CohortDefinition above15Years = cohortDefinitionLibrary.above15Years();



        CohortDefinition males = cohortDefinitionLibrary.males();
        CohortDefinition females = cohortDefinitionLibrary.females();


        CohortDefinition a = df.getPatientsInAll(below1Year, males);
        CohortDefinition b = df.getPatientsInAll(below1Year, females);
        CohortDefinition c = df.getPatientsInAll(between1And4Years, males);
        CohortDefinition d = df.getPatientsInAll(between1And4Years, females);
        CohortDefinition e = df.getPatientsInAll(between5And9Years, males);
        CohortDefinition f = df.getPatientsInAll(between5And9Years, females);
        CohortDefinition g = df.getPatientsInAll(between10And14Years, males);
        CohortDefinition h = df.getPatientsInAll(between10And14Years, females);
        CohortDefinition i = df.getPatientsInAll(between15And19Years, males);
        CohortDefinition j = df.getPatientsInAll(between15And19Years, females);
        CohortDefinition k = df.getPatientsInAll(between20And24Years, males);
        CohortDefinition l = df.getPatientsInAll(between20And24Years, females);
        CohortDefinition m = df.getPatientsInAll(between25And29Years, males);
        CohortDefinition n = df.getPatientsInAll(between25And29Years, females);
        CohortDefinition o = df.getPatientsInAll(between30And34Years, males);
        CohortDefinition p = df.getPatientsInAll(between30And34Years, females);
        CohortDefinition q = df.getPatientsInAll(between35And39Years, males);
        CohortDefinition r = df.getPatientsInAll(between35And39Years, females);
       CohortDefinition s = df.getPatientsInAll(between40And49Years, males);
        CohortDefinition t = df.getPatientsInAll(between40And49Years, females);
        CohortDefinition u= df.getPatientsInAll(above50Years, males);
        CohortDefinition v = df.getPatientsInAll(above50Years, females);

        //106a ageAndGender
        CohortDefinition w = df.getPatientsInAll(below2Years, males);
        CohortDefinition x = df.getPatientsInAll(below2Years, females);
        CohortDefinition y = df.getPatientsInAll(between2And4Years, males);
        CohortDefinition z = df.getPatientsInAll(between2And4Years, females);

        CohortDefinition aa = df.getPatientsInAll(between5And14Years, males);
        CohortDefinition ab = df.getPatientsInAll(between5And14Years, females);
        CohortDefinition ac = df.getPatientsInAll(above15Years, males);
        CohortDefinition ad = df.getPatientsInAll(above15Years, females);


        ageGenderDimension.addParameter(ReportingConstants.END_DATE_PARAMETER);
        ageGenderDimension.addCohortDefinition("below1male", Mapped.mapStraightThrough(a));
        ageGenderDimension.addCohortDefinition("below1female", Mapped.mapStraightThrough(b));
        ageGenderDimension.addCohortDefinition("between1and4male", Mapped.mapStraightThrough(c));
        ageGenderDimension.addCohortDefinition("between1and4female", Mapped.mapStraightThrough(d));
        ageGenderDimension.addCohortDefinition("between5and9male", Mapped.mapStraightThrough(e));
        ageGenderDimension.addCohortDefinition("between5and9female", Mapped.mapStraightThrough(f));
        ageGenderDimension.addCohortDefinition("between10and14male", Mapped.mapStraightThrough(g));
        ageGenderDimension.addCohortDefinition("between10and14female", Mapped.mapStraightThrough(h));
        ageGenderDimension.addCohortDefinition("between15and19male", Mapped.mapStraightThrough(i));
        ageGenderDimension.addCohortDefinition("between15and19female", Mapped.mapStraightThrough(j));
        ageGenderDimension.addCohortDefinition("between20and24male", Mapped.mapStraightThrough(k));
        ageGenderDimension.addCohortDefinition("between20and24female", Mapped.mapStraightThrough(l));
        ageGenderDimension.addCohortDefinition("between25and29male", Mapped.mapStraightThrough(m));
        ageGenderDimension.addCohortDefinition("between25and29female", Mapped.mapStraightThrough(n));
        ageGenderDimension.addCohortDefinition("between30and34male", Mapped.mapStraightThrough(o));
        ageGenderDimension.addCohortDefinition("between30and34female", Mapped.mapStraightThrough(p));
        ageGenderDimension.addCohortDefinition("between35and39male", Mapped.mapStraightThrough(q));
        ageGenderDimension.addCohortDefinition("between35and39female", Mapped.mapStraightThrough(r));
        ageGenderDimension.addCohortDefinition("between40and49male", Mapped.mapStraightThrough(s));
        ageGenderDimension.addCohortDefinition("between40and49female", Mapped.mapStraightThrough(t));
        ageGenderDimension.addCohortDefinition("above50male", Mapped.mapStraightThrough(u));
        ageGenderDimension.addCohortDefinition("above50female", Mapped.mapStraightThrough(v));

        //106a report ages
        ageGenderDimension.addCohortDefinition("below2male", Mapped.mapStraightThrough(w));
        ageGenderDimension.addCohortDefinition("below2female", Mapped.mapStraightThrough(x));
        ageGenderDimension.addCohortDefinition("between2and5male", Mapped.mapStraightThrough(y));
        ageGenderDimension.addCohortDefinition("between2and5female", Mapped.mapStraightThrough(z));
        ageGenderDimension.addCohortDefinition("between5and14male", Mapped.mapStraightThrough(aa));
        ageGenderDimension.addCohortDefinition("between5and14female", Mapped.mapStraightThrough(ab));
        ageGenderDimension.addCohortDefinition("above15male", Mapped.mapStraightThrough(ac));
        ageGenderDimension.addCohortDefinition("above15female", Mapped.mapStraightThrough(ad));

        ageGenderDimension.addCohortDefinition("child", Mapped.mapStraightThrough(cohortDefinitionLibrary.agedBetween(0, 14)));
        ageGenderDimension.addCohortDefinition("adult", Mapped.mapStraightThrough(cohortDefinitionLibrary.agedAtLeast(15)));
        return ageGenderDimension;
    }

    public CohortDefinitionDimension getCBSAdultReportAgeGenderGroup() {
        CohortDefinitionDimension ageGenderDimension = new CohortDefinitionDimension();

        CohortDefinition below5Years = cohortDefinitionLibrary.below5Years();
        CohortDefinition between5And14Years = cohortDefinitionLibrary.between5And14Years();
        CohortDefinition above15Years = cohortDefinitionLibrary.above15Years();

        CohortDefinition males = cohortDefinitionLibrary.males();
        CohortDefinition females = cohortDefinitionLibrary.females();

        CohortDefinition a = df.getPatientsInAll(below5Years, males);
        CohortDefinition b = df.getPatientsInAll(below5Years, females);

        CohortDefinition e = df.getPatientsInAll(between5And14Years, males);
        CohortDefinition f = df.getPatientsInAll(between5And14Years, females);
        CohortDefinition g = df.getPatientsInAll(above15Years, males);
        CohortDefinition h = df.getPatientsInAll(above15Years, females);

        ageGenderDimension.addParameter(ReportingConstants.END_DATE_PARAMETER);
        ageGenderDimension.addCohortDefinition("below5male", Mapped.mapStraightThrough(a));
        ageGenderDimension.addCohortDefinition("below5female", Mapped.mapStraightThrough(b));
        ageGenderDimension.addCohortDefinition("between5and14male", Mapped.mapStraightThrough(e));
        ageGenderDimension.addCohortDefinition("between5and14female", Mapped.mapStraightThrough(f));
        ageGenderDimension.addCohortDefinition("above15male", Mapped.mapStraightThrough(g));
        ageGenderDimension.addCohortDefinition("above15female", Mapped.mapStraightThrough(h));
        return ageGenderDimension;
    }

    public CohortDefinitionDimension getTxNewAgeGenderGroup() {
        CohortDefinitionDimension ageGenderDimension = new CohortDefinitionDimension();

        CohortDefinition below1Year = cohortDefinitionLibrary.below1Year();
        CohortDefinition between1And4Years = cohortDefinitionLibrary.between1And4years();
        CohortDefinition between5And9Years = cohortDefinitionLibrary.between5And9years();
        CohortDefinition between10And14Years = cohortDefinitionLibrary.between10And14years();
        CohortDefinition between15And19Years = cohortDefinitionLibrary.between15And19years();
        CohortDefinition between20And24Years = cohortDefinitionLibrary.between20And24years();
        CohortDefinition between25And49Years =cohortDefinitionLibrary.between25And49years();
        CohortDefinition above50Years = cohortDefinitionLibrary.above50Years();

        //106a age disagregates
        CohortDefinition below2Years = cohortDefinitionLibrary.below2Years();
        CohortDefinition between2And4Years = cohortDefinitionLibrary.between2And5Years();
        CohortDefinition between5And14Years = cohortDefinitionLibrary.between5And14Years();
        CohortDefinition above15Years = cohortDefinitionLibrary.above15Years();



        CohortDefinition males = cohortDefinitionLibrary.males();
        CohortDefinition females = cohortDefinitionLibrary.females();


        CohortDefinition a = df.getPatientsInAll(below1Year, males);
        CohortDefinition b = df.getPatientsInAll(below1Year, females);
        CohortDefinition c = df.getPatientsInAll(between1And4Years, males);
        CohortDefinition d = df.getPatientsInAll(between1And4Years, females);
        CohortDefinition e = df.getPatientsInAll(between5And9Years, males);
        CohortDefinition f = df.getPatientsInAll(between5And9Years, females);
        CohortDefinition g = df.getPatientsInAll(between10And14Years, males);
        CohortDefinition h = df.getPatientsInAll(between10And14Years, females);
        CohortDefinition i = df.getPatientsInAll(between15And19Years, males);
        CohortDefinition j = df.getPatientsInAll(between15And19Years, females);
        CohortDefinition k = df.getPatientsInAll(between20And24Years, males);
        CohortDefinition l = df.getPatientsInAll(between20And24Years, females);
        CohortDefinition m = df.getPatientsInAll(between25And49Years, males);
        CohortDefinition n = df.getPatientsInAll(between25And49Years, females);
        CohortDefinition u= df.getPatientsInAll(above50Years, males);
        CohortDefinition v = df.getPatientsInAll(above50Years, females);

        //106a ageAndGender
        CohortDefinition w = df.getPatientsInAll(below2Years, males);
        CohortDefinition x = df.getPatientsInAll(below2Years, females);
        CohortDefinition y = df.getPatientsInAll(between2And4Years, males);
        CohortDefinition z = df.getPatientsInAll(between2And4Years, females);

        CohortDefinition aa = df.getPatientsInAll(between5And14Years, males);
        CohortDefinition ab = df.getPatientsInAll(between5And14Years, females);
        CohortDefinition ac = df.getPatientsInAll(above15Years, males);
        CohortDefinition ad = df.getPatientsInAll(above15Years, females);


        ageGenderDimension.addParameter(ReportingConstants.END_DATE_PARAMETER);
        ageGenderDimension.addCohortDefinition("below1male", Mapped.mapStraightThrough(a));
        ageGenderDimension.addCohortDefinition("below1female", Mapped.mapStraightThrough(b));
        ageGenderDimension.addCohortDefinition("between1and4male", Mapped.mapStraightThrough(c));
        ageGenderDimension.addCohortDefinition("between1and4female", Mapped.mapStraightThrough(d));
        ageGenderDimension.addCohortDefinition("between5and9male", Mapped.mapStraightThrough(e));
        ageGenderDimension.addCohortDefinition("between5and9female", Mapped.mapStraightThrough(f));
        ageGenderDimension.addCohortDefinition("between10and14male", Mapped.mapStraightThrough(g));
        ageGenderDimension.addCohortDefinition("between10and14female", Mapped.mapStraightThrough(h));
        ageGenderDimension.addCohortDefinition("between15and19male", Mapped.mapStraightThrough(i));
        ageGenderDimension.addCohortDefinition("between15and19female", Mapped.mapStraightThrough(j));
        ageGenderDimension.addCohortDefinition("between20and24male", Mapped.mapStraightThrough(k));
        ageGenderDimension.addCohortDefinition("between20and24female", Mapped.mapStraightThrough(l));
        ageGenderDimension.addCohortDefinition("between25and49male", Mapped.mapStraightThrough(m));
        ageGenderDimension.addCohortDefinition("between25and49female", Mapped.mapStraightThrough(n));
        ageGenderDimension.addCohortDefinition("above50male", Mapped.mapStraightThrough(u));
        ageGenderDimension.addCohortDefinition("above50female", Mapped.mapStraightThrough(v));

        //106a report ages
        ageGenderDimension.addCohortDefinition("below2male", Mapped.mapStraightThrough(w));
        ageGenderDimension.addCohortDefinition("below2female", Mapped.mapStraightThrough(x));
        ageGenderDimension.addCohortDefinition("between2and5male", Mapped.mapStraightThrough(y));
        ageGenderDimension.addCohortDefinition("between2and5female", Mapped.mapStraightThrough(z));
        ageGenderDimension.addCohortDefinition("between5and14male", Mapped.mapStraightThrough(aa));
        ageGenderDimension.addCohortDefinition("between5and14female", Mapped.mapStraightThrough(ab));
        ageGenderDimension.addCohortDefinition("above15male", Mapped.mapStraightThrough(ac));
        ageGenderDimension.addCohortDefinition("above15female", Mapped.mapStraightThrough(ad));

        ageGenderDimension.addCohortDefinition("child", Mapped.mapStraightThrough(cohortDefinitionLibrary.agedBetween(0, 14)));
        ageGenderDimension.addCohortDefinition("adult", Mapped.mapStraightThrough(cohortDefinitionLibrary.agedAtLeast(15)));

        ageGenderDimension.addCohortDefinition("male",Mapped.mapStraightThrough(males));
        ageGenderDimension.addCohortDefinition("female",Mapped.mapStraightThrough(females));
        return ageGenderDimension;

    }



    public CohortDefinitionDimension getAdherenceGroup() {
        CohortDefinitionDimension adherenceDimension = new CohortDefinitionDimension();

        CohortDefinition good = df.getPatientsWithCodedObsDuringPeriod(hivMetadata.getAdherence(), Arrays.asList(hivMetadata.getARTEncounterEncounterType()), Arrays.asList(hivMetadata.getGoodAdherence()), BaseObsCohortDefinition.TimeModifier.ANY);
        CohortDefinition fair = df.getPatientsWithCodedObsDuringPeriod(hivMetadata.getAdherence(), Arrays.asList(hivMetadata.getARTEncounterEncounterType()), Arrays.asList(hivMetadata.getFairAdherence()), BaseObsCohortDefinition.TimeModifier.ANY);
        CohortDefinition poor = df.getPatientsWithCodedObsDuringPeriod(hivMetadata.getAdherence(), Arrays.asList(hivMetadata.getARTEncounterEncounterType()), Arrays.asList(hivMetadata.getPoorAdherence()), BaseObsCohortDefinition.TimeModifier.ANY);

        adherenceDimension.addParameter(ReportingConstants.END_DATE_PARAMETER);
        adherenceDimension.addCohortDefinition("good", Mapped.mapStraightThrough(good));
        adherenceDimension.addCohortDefinition("fair", Mapped.mapStraightThrough(fair));
        adherenceDimension.addCohortDefinition("poor", Mapped.mapStraightThrough(poor));
        return adherenceDimension;
    }


    public CohortDefinitionDimension get106bEMTCTGroup() {
        CohortDefinitionDimension eMTCTDimension = new CohortDefinitionDimension();

        CohortDefinition pregnant = hivCohortDefinitionLibrary.getPregnantPatientsAtArtStart();
        CohortDefinition lactating = hivCohortDefinitionLibrary.getLactatingPatientsAtArtStart();
        CohortDefinition pregnantOrLactating = df.getPatientsInAny(pregnant, lactating);

        eMTCTDimension.addCohortDefinition("pregnant", Mapped.mapStraightThrough(pregnantOrLactating));

        return eMTCTDimension;
    }

}
