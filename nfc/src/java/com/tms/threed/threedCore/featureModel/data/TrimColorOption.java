package com.tms.threed.threedCore.featureModel.data;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

/**
 * WB, CF, 040, 13, 2513
 */
public class TrimColorOption extends TrimColor {

    //options (directly pickable)
    public Var options = addVar(Options);

    public Var aw = options.addChild("AW");
    public Var up = options.addChild("UP", "Hyb Upgrade Package");
    public Var ut = options.addChild("UT", "Hyb Upgrade Package with Nav");
    public Var sr = options.addChild("SR");

    public Var nv = options.addChild("NV");
    public Var la = options.addChild("LA", "Leather Package (SE / Hybrid)");
    public Var cq = options.addChild("CQ", "Comfort and Convenience Package");
    public Var qe = options.addChild("QE");

    public Var qa = options.addChild("QA");
    public Var qb = options.addChild("QB");
    public Var qc = options.addChild("QC", "Sport Leather Package (SE)");
    public Var qd = options.addChild("QD", "SE Extra Value Package");
    public Var qf = options.addChild("QF");

    public Var ex = options.addChild("EX", "DLX USB Audio cd with 6 speakers");
    public Var sportPedals = options.addChild("32", "Sport Pedals");


    //options (derived)
    public Var camera = options.addChild("Camera");
    public Var buttons = options.addChild("Buttons", "Door Handle Buttons");
    public Var startButton = options.addChild("StartButton");
    public Var chrome = options.addChild("Chrome");
    public Var rearAC = options.addChild("RearAC", "Rear AC Vents");
    public Var satAntenna = options.addChild("Antenna", "Satellite Radio Antenna");
    public Var wood = options.addChild("Wood", "Wood Trim");
    public Var leatherWheel = options.addChild("LeatherWheel");
    public Var acButton = options.addChild("ACButton");
    public Var btButton = options.addChild("BTButton");
    public Var sk = options.addChild("SK");

    public Var hd = options.addChild("HD", "Heated Seats");

    Var ec = options.addChild("EC");
    Var ej = options.addChild("EJ", "JBL Radio");
    Var lf = options.addChild("LF");

    public void test() throws Exception {


    }


    public TrimColorOption() {
        options.setMandatory(true);


        addConstraint(iff(sportPedals, se));

        addConstraint(imply(cq, and(hd, hybrid)));

        addConstraint(iff(qa, and(aw, le)));

        addConstraint(iff(qb, and(ec, se, at6)));


        addConstraint(conflict(base, sr));
        addConstraint(imply(xle, sr));

        addConstraint(conflict(nv, or(base, le)));
        addConstraint(conflict(nv, or(ej, ex)));


        addConstraint(imply(la, leather));//added by dave
        addConstraint(conflict(la, or(base, le)));
        addConstraint(conflict(la, or(and(ash, beach), and(ash, green), and(bisque, silver))));


        addConstraint(imply(qc, se));

        addConstraint(imply(qc, and(hd, la)));


        addConstraint(imply(qe, nv));
        addConstraint(conflict(qe, or(base, le, hybrid)));

        addConstraint(iff(qf, and(hd, sk, xle)));

        addConstraint(imply(hd, or(cq, qc, qf))); //added by dave
        addConstraint(conflict(hd, base));
        addConstraint(conflict(hd, le));

        addConstraint(conflict(ec, base));

        addConstraint(imply(xle, ec));


        addConstraint(conflict(ej, or(ex, nv)));
        addConstraint(conflict(ex, or(ej, nv)));

        addConstraint(conflict(xle, ex));


        addConstraint(conflict(sk, or(base, le, mt6)));

        addConstraint(conflict(lf, or(base, le)));
        addConstraint(imply(se, lf));

        flashKeyConstraints();

    }

    private void flashKeyConstraints() {
        addConstraint(imply(nv, and(camera, satAntenna, btButton)));
        addConstraint(imply(xle, and(chrome, rearAC, wood, acButton)));
        addConstraint(imply(hybrid, and(chrome, rearAC, acButton, sk)));

        addConstraint(imply(up, and(ec, ej, leatherWheel, hybrid)));

        addConstraint(imply(ut, and(nv, ec, leatherWheel, hybrid)));

        addConstraint(imply(ej, satAntenna));
        addConstraint(imply(ej, btButton));

        addConstraint(iff(sk, and(buttons, startButton)));

        addConstraint(imply(camera, nv));
        addConstraint(imply(acButton, or(xle, hybrid)));

        addConstraint(imply(buttons, sk));
        addConstraint(imply(startButton, sk));

        addConstraint(imply(ex, satAntenna));
        addConstraint(imply(ex, btButton));
        addConstraint(conflict(camera, or(base, le)));

        addConstraint(conflict(chrome, or(base, le, se)));
        addConstraint(conflict(rearAC, or(base, le, se)));
        addConstraint(conflict(wood, or(base, le)));


        addConstraint(imply(wood, or(xle, la)));

        addConstraint(imply(leatherWheel, hybrid));
        addConstraint(imply(leatherWheel, or(up, ut)));


        addConstraint(conflict(btButton, or(base, le)));
        addConstraint(imply(btButton, or(nv, ej, ex)));

        addConstraint(imply(satAntenna, or(nv, ej, ex)));

        addConstraint(imply(la, wood));
    }

}
