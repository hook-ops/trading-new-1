package com.biiuse.motivewave;

import com.motivewave.platform.sdk.common.*;
import com.motivewave.platform.sdk.common.Enums.BarSizeType;
import com.motivewave.platform.sdk.common.desc.*;
import com.motivewave.platform.sdk.common.menu.MenuDescriptor;
import com.motivewave.platform.sdk.common.menu.MenuItem;
import com.motivewave.platform.sdk.common.menu.MenuSeparator;
import com.motivewave.platform.sdk.draw.Figure;
import com.motivewave.platform.sdk.draw.Marker;
import com.motivewave.platform.sdk.order_mgmt.Order;
import com.motivewave.platform.sdk.order_mgmt.OrderContext;
import com.motivewave.platform.sdk.study.Plot;
import com.motivewave.platform.sdk.study.RuntimeDescriptor;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

@StudyHeader(
        namespace="com.motivewave",
        id="test-1",
        rb="com.motivewave.platform.study.nls.strings2",
        name="test-1",
        desc="",
        menu="Val Baur",
        overlay=true,
        signals=true,
        autoEntry = true,
        supportsUnrealizedPL = true,
        supportsRealizedPL = true,
        supportsTotalPL = true,
        supportsUseAccountPosition = true,
        supportsSessions = true,
        supportsCloseOnDeactivate = true,
        requiresBarUpdates = true,
        strategy=true,
        studyOverlay=true)

public class ITG_Strategy_Automatic extends Study
{
    Figure StrategyLight = null;
    enum Values { SRO, LongMarkerDrawn, ShortMarkerDrawn, BottomHack, TopHack, TSL, UP, DOWN, TREND };
    enum Signals { SELL, BUY, ST_SELL, ST_BUY };
    private static final String SHOW_DAY_PNL = "SHOW_DAY_PNL";
    private static final String DAY_PNL_FONT = "DAY_PNL_FONT";
    OrderContext oCtx = null;
    private static final String STOP_LOSS_TICKS = "STOP_LOSS_TICKS";
    private static final String TARGET_TICKS = "TARGET_TICKS";
    private static final String USE_TARGET = "USE_TARGET";
    private static final String USE_STOP_LOSS = "USE_STOP_LOSS";

    // Add By Nia 10/12/2024
    private static final String ADDITIONAL_ORDER_1 = "ADDITIONAL_ORDER_1";
    private static final String ADDITIONAL_ORDER_TICKS_1 = "ADDITIONAL_ORDER_TICKS_1";

    private static final String ADDITIONAL_ORDER_2 = "ADDITIONAL_ORDER_2";
    private static final String ADDITIONAL_ORDER_TICKS_2 = "ADDITIONAL_ORDER_TICKS_2";

    private static final String ADDITIONAL_ORDER_3 = "ADDITIONAL_ORDER_3";
    private static final String ADDITIONAL_ORDER_TICKS_3 = "ADDITIONAL_ORDER_TICKS_3";

    private static final String ADDITIONAL_ORDER_4 = "ADDITIONAL_ORDER_4";
    private static final String ADDITIONAL_ORDER_TICKS_4 = "ADDITIONAL_ORDER_TICKS_4";

    private static final String ADDITIONAL_ORDER_5 = "ADDITIONAL_ORDER_5";
    private static final String ADDITIONAL_ORDER_TICKS_5 = "ADDITIONAL_ORDER_TICKS_5";

    private static final String ADDITIONAL_ORDER_6 = "ADDITIONAL_ORDER_6";
    private static final String ADDITIONAL_ORDER_TICKS_6 = "ADDITIONAL_ORDER_TICKS_6";

    private static final String ADDITIONAL_ORDER_7 = "ADDITIONAL_ORDER_7";
    private static final String ADDITIONAL_ORDER_TICKS_7 = "ADDITIONAL_ORDER_TICKS_7";

    private static final String ADDITIONAL_ORDER_8 = "ADDITIONAL_ORDER_8";
    private static final String ADDITIONAL_ORDER_TICKS_8 = "ADDITIONAL_ORDER_TICKS_8";

    private static final String ADDITIONAL_ORDER_9 = "ADDITIONAL_ORDER_9";
    private static final String ADDITIONAL_ORDER_TICKS_9 = "ADDITIONAL_ORDER_TICKS_9";

    private static final String ADDITIONAL_ORDER_10 = "ADDITIONAL_ORDER_10";
    private static final String ADDITIONAL_ORDER_TICKS_10 = "ADDITIONAL_ORDER_TICKS_10";



    private static final String ENTRY_TICKS = "ENTRY_TICKS";


    private static final String ORDER_TYPE = "ORDER_TYPE";
    private static final String ENTRY_TIME = "ENTRY_TIME";
    private static final String PROFIT_TARGET_DOLLARS = "PROFIT_TARGET_DOLLARS";
    private static final String STOP_LOSS_DOLLARS = "STOP_LOSS_DOLLARS";
    private static final String BARSIZE = "BARSIZE =";
    private static final String MAX_OPEN_POSITION = "MAX_OPEN_POSITION";
    private static final String TRADE_DIRECTION = "TRADE_DIRECTION";
    private static final String ST_MULTIPLIER = "ST_MULTIPLIER";
    private static final String ST_PERIOD = "ST_PERIOD";
    private static final String REQUIRE_SUPERTREND_CONSISTENCY = "REQUIRE_SUPERTREND_CONSISTENCY";
    private static final String INTRABAR_EXIT_ON_SUPERTREND_OPPOSITE_TREND = "INTRABAR_EXIT_ON_SUPERTREND_OPPOSITE_TREND";
    private static final String EOB_EXIT_ON_SUPERTREND_OPPOSITE_TREND = "EOB_EXIT_ON_SUPERTREND_OPPOSITE_TREND";

    private static final List<NVP> OrderType = List.of(
            new NVP("Market", "Market"),
            new NVP("Limit", "Limit"));

    private static final List<NVP> EntryTime = List.of(
            new NVP("Intrabar", "Intrabar"),
            new NVP("End of Bar", "End of Bar"));

    private static final List<NVP> TradeDirection = List.of(
            new NVP("Long Only", "Long Only"),
            new NVP("Short Only", "Short Only"),
            new NVP("Long and Short", "Long and Short")
    );

    double highSell=Double.NEGATIVE_INFINITY;
    double lowBuy=Double.MAX_VALUE;

    @Override
    public void onActivate(OrderContext ctx) {
        oCtx = ctx;
        placedBuyEntry=false;
        placedSellEntry=false;
        entriesPlaced = 0;
        dayPnL = 0d;
        openPositions = 0;
        originalSignalPrice = Float.NaN;
        currentDay = 0L;
        currentTime=0L;
        SuperTrendDirection=0;
        intrabarMarkerCache=null;
        intrabarMarkerIndCache=-1;
        markBarCloseForST_Buy=false;
        markBarCloseForST_Sell=false;
        dayPnL=0d;
        setEntryState(Enums.EntryState.NONE);

        super.onActivate(ctx);
    }

    @Override
    public void onReset(OrderContext ctx) {
        super.onReset(ctx);
    }

    @Override
    public void onDeactivate(OrderContext ctx) {

        if (!ctx.getActiveOrders().isEmpty()) ctx.cancelOrders();

        super.onDeactivate(ctx);

        placedBuyEntry=false;
        placedSellEntry=false;
        entriesPlaced = 0;
        //dayPnL = 0d;
        openPositions = 0;
        originalSignalPrice = Float.NaN;
        //currentDay = 0L;
        currentTime=0L;

        setEntryState(Enums.EntryState.NONE);

    }

    @Override
    public void onPositionClosed(OrderContext ctx) {
        super.onPositionClosed(ctx);
        boolean existingSession=false;
        // for(TimeFrame tf : getSettings().getSessions()) if (tf.isEnabled()) existingSession=true;
        // if(getSettings().getSessions().size() > 0 && existingSession)
        dayPnL+=ctx.getRealizedPnL();
        resetInternals(ctx);
        if (!ctx.getActiveOrders().isEmpty()) ctx.cancelOrders();
    }

    @Override
    public void initialize(Defaults defaults)
    {
        SettingsDescriptor sd=new SettingsDescriptor();
        setSettingsDescriptor(sd);

        SettingTab tab=new SettingTab("Strategy Inputs");
        sd.addTab(tab);
        SettingGroup strategy_inputs1 = new SettingGroup("Inputs");
        strategy_inputs1.addRow(new DiscreteDescriptor(TRADE_DIRECTION, "Trade Direction", "Long and Short", TradeDirection));
        strategy_inputs1.addRow(new DiscreteDescriptor(ORDER_TYPE, "Entry Order Type", "Market", OrderType));
        strategy_inputs1.addRow(new DiscreteDescriptor(ENTRY_TIME, "Entry Time", "End of Bar", EntryTime));
        strategy_inputs1.addRow(new DoubleDescriptor(PROFIT_TARGET_DOLLARS, "Session Profit Target ($)", 2000d, 0, 999999999, 0.01d));
        strategy_inputs1.addRow(new DoubleDescriptor(STOP_LOSS_DOLLARS, "Session Stop Loss ($)", -2000d, -999999999, 0, 0.01d));

        strategy_inputs1.addRow(new IntegerDescriptor(ENTRY_TICKS, "Buffer for Initial Entry Limit Order (in Ticks)", 0, 1, 999999999, 1));


        // Add By Nia 10/11/2024
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_1, "Level 1 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_1, "Level 1 Buffer (ticks)", 0, 0, 999999999, 1));
        
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_2, "Level 2 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_2, "Level 2 Buffer (ticks)", 0, 0, 999999999, 1));
        
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_3, "Level 3 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_3, "Level 3 Buffer (ticks)", 0, 0, 999999999, 1));
        
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_4, "Level 4 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_4, "Level 4 Buffer (ticks)", 0, 0, 999999999, 1));
        
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_5, "Level 5 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_5, "Level 5 Buffer (ticks)", 0, 0, 999999999, 1));
        
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_6, "Level 6 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_6, "Level 6 Buffer (ticks)", 0, 0, 999999999, 1));
        
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_7, "Level 7 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_7, "Level 7 Buffer (ticks)", 0, 0, 999999999, 1));
        
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_8, "Level 8 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_8, "Level 8 Buffer (ticks)", 0, 0, 999999999, 1));
        
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_9, "Level 9 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_9, "Level 9 Buffer (ticks)", 0, 0, 999999999, 1));
        
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_10, "Level 10 Order", 0, 0, 999999999, 1));
        strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS_10, "Level 10 Buffer (ticks)", 0, 0, 999999999, 1));
        
        // ========================================================================================
       

        // strategy_inputs1.addRow(new IntegerDescriptor(ADDITIONAL_ORDER_TICKS, "Additional Orders Buffer (in Ticks)", 4, 1, 999999999, 1));
        strategy_inputs1.addRow(new BooleanDescriptor(USE_STOP_LOSS,"Use Stop Loss",true));
        strategy_inputs1.addRow(new IntegerDescriptor(STOP_LOSS_TICKS, "Stop Loss (in Ticks)", 10, 1, 999999999, 1));
        strategy_inputs1.addRow(new BooleanDescriptor(USE_TARGET,"Use Profit Target",true));
        strategy_inputs1.addRow(new IntegerDescriptor(TARGET_TICKS, "Profit Target (in Ticks)", 20, 1, 999999999, 1));
        sd.addDependency(new EnabledDependency(USE_TARGET, TARGET_TICKS));
        sd.addDependency(new EnabledDependency(USE_STOP_LOSS, STOP_LOSS_TICKS));
        strategy_inputs1.addRow(new IntegerDescriptor(MAX_OPEN_POSITION, "Maximum Open Positions", 20, 0, 999999999, 1));
        SettingGroup day_realized_pnL = new SettingGroup("Day Realized PnL");
        day_realized_pnL.addRow(new BooleanDescriptor(SHOW_DAY_PNL, "Show Day Realized PnL", true),
                new FontDescriptor(DAY_PNL_FONT, "Font", new Font("CQG Swiss", Font.BOLD, 14), X11Colors.WHITE_SMOKE,
                        true, X11Colors.BLACK, true, true, true));
        sd.addDependency(new EnabledDependency(SHOW_DAY_PNL, DAY_PNL_FONT));
        tab.addGroup(strategy_inputs1);
        tab.addGroup(day_realized_pnL);
        sd.addInvisibleSetting(new BarSizeDescriptor(BARSIZE,"Hidden BarSize 1", BarSize.getBarSize(BarSizeType.LINEAR, Enums.IntervalType.DAY,1)));
        strategy_inputs1 = new SettingGroup("SuperTrend Options");
        tab.addGroup(strategy_inputs1);
        strategy_inputs1.addRow(new BooleanDescriptor(REQUIRE_SUPERTREND_CONSISTENCY,"Require SuperTrend Consistency for Entry",false));
        strategy_inputs1.addRow(new BooleanDescriptor(INTRABAR_EXIT_ON_SUPERTREND_OPPOSITE_TREND,"Intrabar Exit on Opposite SuperTrend",false));
        strategy_inputs1.addRow(new BooleanDescriptor(EOB_EXIT_ON_SUPERTREND_OPPOSITE_TREND,"End-of-Bar Exit on Opposite SuperTrend",false));


        tab=new SettingTab("SRO General");
        sd.addTab(tab);

        SettingGroup inputs=new SettingGroup(get("INPUTS"));
        inputs.addRow(new InputDescriptor(Inputs.INPUT, get("INPUT"), Enums.BarInput.CLOSE));
        tab.addGroup(inputs);

        SettingGroup settings=new SettingGroup(get("COLORS"));
        settings.addRow(new ColorDescriptor(Inputs.UP_COLOR, get("LBL_UP_COLOR"), defaults.getGreen()));
        settings.addRow(new ColorDescriptor(Inputs.NEUTRAL_COLOR, get("LBL_NEUTRAL_COLOR"), defaults.getBlue(), true, true));
        settings.addRow(new ColorDescriptor(Inputs.DOWN_COLOR, get("LBL_DOWN_COLOR"), defaults.getRed()));
        settings.addRow(new IndicatorDescriptor(Inputs.IND, get("IND"), defaults.getLineColor(), null, false, true, true));
        tab.addGroup(settings);

        SettingGroup markers=new SettingGroup(get("MARKERS"));
        markers.addRow(new MarkerDescriptor(Inputs.UP_MARKER, get("UP_MARKER"), Enums.MarkerType.TRIANGLE,
                Enums.Size.VERY_SMALL, defaults.getGreen(), defaults.getLineColor(), true, true));
        markers.addRow(new MarkerDescriptor(Inputs.DOWN_MARKER, get("DOWN_MARKER"), Enums.MarkerType.TRIANGLE,
                Enums.Size.VERY_SMALL, defaults.getRed(), defaults.getLineColor(), true, true));
        tab.addGroup(markers);

        tab=new SettingTab("SRO Display");
        sd.addTab(tab);

        SettingGroup guides=new SettingGroup(get("GUIDES"));
        GuideDescriptor topDesc=new GuideDescriptor(Inputs.TOP_GUIDE, get("TOP_GUIDE"), .7, 0, 1.01, .01, true);
        topDesc.setLineColor(defaults.getRed());
        guides.addRow(topDesc);
        GuideDescriptor mg=new GuideDescriptor(Inputs.MIDDLE_GUIDE, get("MIDDLE_GUIDE"), .5, 0, 1.01, .01, true);
        mg.setDash(new float[] { 3, 3 });
        guides.addRow(mg);
        GuideDescriptor bottomDesc=new GuideDescriptor(Inputs.BOTTOM_GUIDE, get("BOTTOM_GUIDE"), .3, 0, 1.01, .01, true);
        bottomDesc.setLineColor(defaults.getGreen());
        guides.addRow(bottomDesc);
        tab.addGroup(guides);

        tab = new SettingTab("SuperTrend General");
        sd.addTab(tab);
        inputs=new SettingGroup("Inputs");
        inputs.addRow(new DoubleDescriptor(ST_MULTIPLIER, get("Multiplier"), 2, 0.05, 10, 0.05));
        inputs.addRow(new IntegerDescriptor(ST_PERIOD, get("Period"), 7, 1, 20, 1));
        tab.addGroup(inputs);

        tab = new SettingTab("SuperTrend Display");
        sd.addTab(tab);
        inputs=new SettingGroup(get("LBL_DISPLAY"));
        PathDescriptor path = new PathDescriptor(Inputs.PATH, get("LBL_LINE"), defaults.getLineColor(), 1.0f, null, true, false, true);
        path.setSupportsColor(false);
        path.setSupportsShadeType(false);
        inputs.addRow(path);
        inputs.addRow(new ColorDescriptor(Inputs.UP_COLOR, get("LBL_UP_COLOR"), defaults.getGreenLine(),  true, false));
        inputs.addRow(new ColorDescriptor(Inputs.DOWN_COLOR, get("LBL_DOWN_COLOR"), defaults.getRedLine(),  true, false));
        inputs.addRow(new MarkerDescriptor(Inputs.UP_MARKER, get("Up Marker"),
                Enums.MarkerType.ARROW, Enums.Size.SMALL, defaults.getGreen(), defaults.getLineColor(), true, true));
        inputs.addRow(new MarkerDescriptor(Inputs.DOWN_MARKER, get("Down Marker"),
                Enums.MarkerType.ARROW, Enums.Size.SMALL, defaults.getRed(), defaults.getLineColor(), true, true));
        inputs.addRow(new IndicatorDescriptor(Inputs.IND, get("LBL_INDICATOR"), null, null, false, true, true));
        tab.addGroup(inputs);

        RuntimeDescriptor desc=new RuntimeDescriptor();
        Plot indicatorPlot = new Plot();

        indicatorPlot.setLabelSettings(TRADE_DIRECTION, ORDER_TYPE, ENTRY_TIME, PROFIT_TARGET_DOLLARS, STOP_LOSS_DOLLARS);

        desc.exportValue(new ValueDescriptor(Values.SRO, get("SRO"), new String[] { Inputs.INPUT }));
        desc.exportValue(new ValueDescriptor(Signals.SELL, Enums.ValueType.BOOLEAN, get("SELL"), null));
        desc.exportValue(new ValueDescriptor(Signals.BUY, Enums.ValueType.BOOLEAN, get("BUY"), null));

        indicatorPlot.declareBars(Values.SRO);
        indicatorPlot.declareIndicator(Values.SRO, Inputs.IND);

        desc.declareSignal(Signals.SELL, get("SELL"));
        desc.declareSignal(Signals.BUY, get("BUY"));

        indicatorPlot.setRangeKeys(Values.SRO, Values.BottomHack, Values.TopHack);
        indicatorPlot.setTabName("Indicator");
        indicatorPlot.setLabelPrefix("Indicator");
        indicatorPlot.setTopInsetPixels(5);
        indicatorPlot.setBottomInsetPixels(0);
        desc.addPlot("Indicator", indicatorPlot);

        desc.exportValue(new ValueDescriptor(Values.TSL, "SuperTrend", new String[] {ST_MULTIPLIER, ST_PERIOD}));
        desc.declarePath(Values.TSL, Inputs.PATH);
        desc.declareIndicator(Values.TSL, Inputs.IND);
        desc.declareSignal(Signals.ST_BUY, get("SuperTrend Buy"));
        desc.declareSignal(Signals.ST_SELL, get("SuperTrend Sell"));

        setRuntimeDescriptor(desc);
    }

    public class RoundedRectangle extends Figure {

        long x1,x2;
        Double y1,y2;
        Color fillColor;
        int radius = 40;


        public RoundedRectangle(Color fillColor){
            this.fillColor = fillColor;
        }

        @Override
        public void draw(Graphics2D g2d, DrawContext ctx) {


            if (getSettings().getBoolean(SHOW_DAY_PNL)) {
                FontInfo f = getSettings().getFont(DAY_PNL_FONT);
                FontMetrics fm = g2d.getFontMetrics(f.getFont());
                String st = Util.round(dayPnL,2)+"";
                int w = fm.stringWidth(st);

                Rectangle2D b = ctx.getBounds();
                double x1_ = b.getMaxX()-5;
                double y1_ = b.getMinY();

                g2d.setFont(f.getFont());
                g2d.setColor(f.getColor());
                g2d.drawString(st, (float)b.getMaxX() - w - 20, (float)y1_ + 20 + fm.getMaxAscent());
            }

        }
    }

    @Override
    public void onLoad(Defaults defaults)
    {
        if (StrategyLight == null) {
            StrategyLight = new RoundedRectangle(X11Colors.LIME_GREEN);
            addFigure(StrategyLight);
        }
        if (getSettings().getSessions().size() == 2)
            getSettings().addSession(new TimeFrame());
        setMinBars(100);
        placedBuyEntry=false;
        placedSellEntry=false;
        entriesPlaced = 0;
        dayPnL = 0d;
        openPositions = 0;
        originalSignalPrice = Float.NaN;
        currentDay = 0L;
        currentTime = 0L;
        limitOrders = false;
        stopTicks = 50;
        useTarget=true;
        useStopLoss = true;
        profitTicks = 20;


        // Add By Nia 10/10/2024
        additionalOrder1 = 0;
        additionalOrder2 = 0;
        additionalOrder3 = 0;
        additionalOrder4 = 0;
        additionalOrder5 = 0;
        additionalOrder6 = 0;
        additionalOrder7 = 0;
        additionalOrder8 = 0;
        additionalOrder9 = 0;
        additionalOrder10 = 0;

        additionalOrderTicks1 = 0;
        additionalOrderTicks2 = 0;
        additionalOrderTicks3 = 0;
        additionalOrderTicks4 = 0;
        additionalOrderTicks5 = 0;
        additionalOrderTicks6 = 0;
        additionalOrderTicks7 = 0;
        additionalOrderTicks8 = 0;
        additionalOrderTicks9 = 0;
        additionalOrderTicks10 = 0;

        // =========================
        entryTicks = 4;
        maxOpenPositions=20;
        maxProfit=2000d;
        oCtx = null;
        maxLoss=-2000d;
        entryTime = "End of Bar";
        SuperTrendDirection=0;
        intrabarMarkerCache=null;
        intrabarMarkerIndCache=-1;
        markBarCloseForST_Buy=false;
        markBarCloseForST_Sell=false;
        loadSettings();

    }

    // @Override
    // public ITG_Strategy_Automatic clone() {
    //     ITG_Strategy_Automatic study = null;
    //     study = (ITG_Strategy_Automatic) super.clone();
    //     study.placedBuyEntry=false;
    //     study.placedSellEntry=false;
    //     study.entriesPlaced = 0;
    //     study.dayPnL = 0d;
    //     study.oCtx = null;
    //     study.entryTime = "End of Bar";
    //     study.openPositions = 0;
    //     study.originalSignalPrice = Float.NaN;
    //     study.currentDay = 0L;
    //     study.currentTime = 0L;
    //     study.limitOrders = false;
    //     study.stopTicks = 50;
    //     study.useStopLoss=true;
    //     study.useTarget=true;
    //     study.profitTicks = 20;

    //     study.additionalOrderTicks = 10;

    //     study.entryTicks = 4;
    //     study.maxOpenPositions=20;
    //     study.maxProfit=2000d;
    //     study.maxLoss=-2000d;
    //     study.SuperTrendDirection=0;
    //     study.intrabarMarkerCache=null;
    //     study.intrabarMarkerIndCache=-1;
    //     study.markBarCloseForST_Buy=false;
    //     study.markBarCloseForST_Sell=false;
    //     return study;
    // }

    private int SuperTrendDirection=0;
    @Override
    protected void calculate(int index, DataContext ctx)
    {
        if (!getFigures().contains(StrategyLight)) addFigure(StrategyLight);

        loadSettings();
        DataSeries series=ctx.getDataSeries();
        if (index < 1) return;
        Object input=getSettings().getInput(Inputs.INPUT, Enums.BarInput.CLOSE);
        double sro=0.0;
        double rHigh=series.getHigh(index); // getRealHigh(index);
        double rLow=series.getLow(index); // getRealLow(index);
        double open=series.getOpen(index);
        double close=series.getDouble(index, input, 0);
        double tRange=series.getTrueRange(index);
        if (tRange != 0.0) sro=((rHigh - open) + (close - rLow)) / (2 * tRange);
        series.setDouble(index, Values.SRO, sro);
        series.setDouble(index, Values.BottomHack, 1.0);
        series.setDouble(index, Values.TopHack, -.1);

        GuideInfo topGuide=getSettings().getGuide(Inputs.TOP_GUIDE);
        double topG=topGuide.getValue();
        GuideInfo bottomGuide=getSettings().getGuide(Inputs.BOTTOM_GUIDE);
        double bottG=bottomGuide.getValue();
        Color upC=getSettings().getColor(Inputs.UP_COLOR);
        Color nC=getSettings().getColor(Inputs.NEUTRAL_COLOR);
        Color dnC=getSettings().getColor(Inputs.DOWN_COLOR);
        series.setBarColor(index, Values.SRO, nC);
        if (sro > topG) series.setBarColor(index, Values.SRO, upC);
        if (sro < bottG) series.setBarColor(index, Values.SRO, dnC);
        // Check for signal events

        boolean sell=(sro > topG) && (sro > highSell); // && (prevSro > sro)
        boolean buy=(sro < bottG) && (sro < lowBuy); // && (prevSro < sro)

        series.setBoolean(index, Signals.SELL, sell);
        series.setBoolean(index, Signals.BUY, buy);


        if (entryTime.equals("End of Bar") && !series.isBarComplete(index)) {

        } else {
            String td = getSettings().getString(TRADE_DIRECTION);
            boolean takeLongs = td.equals("Long and Short") || td.equals("Long Only");
            boolean takeShorts = td.equals("Long and Short") || td.equals("Short Only");

            if (sell) {
                lowBuy = Double.MAX_VALUE;
                highSell = sro;
                Coordinate c = new Coordinate(series.getStartTime(index), sro);
                MarkerInfo marker = getSettings().getMarker(Inputs.DOWN_MARKER);
                String msg = get("SELL_HIGH_SRO", Util.round(rHigh, 2), Util.round(sro, 3));
                if (marker.isEnabled() && !series.getBoolean(index, Values.ShortMarkerDrawn, false)) {
                    series.setBoolean(index, Values.ShortMarkerDrawn, true);
                    addFigure("Indicator", new Marker(c, Enums.Position.TOP, marker, msg));
                }
                if (takeShorts) ctx.signal(index, Signals.SELL, msg, rHigh);
            }
            if (buy) {
                highSell = Double.NEGATIVE_INFINITY;
                lowBuy = sro;
                Coordinate c = new Coordinate(series.getStartTime(index), sro);
                MarkerInfo marker = getSettings().getMarker(Inputs.UP_MARKER);
                String msg = get("BUY_LOW_SRO", Util.round(rLow, 2), Util.round(sro, 3));
                if (marker.isEnabled() && !series.getBoolean(index, Values.LongMarkerDrawn, false)) {
                    series.setBoolean(index, Values.LongMarkerDrawn, true);
                    addFigure("Indicator", new Marker(c, Enums.Position.BOTTOM, marker, msg));
                }
                if (takeLongs) ctx.signal(index, Signals.BUY, msg, rLow);
            }
        }

        /// SUPER TREND
        double mult = getSettings().getDouble(ST_MULTIPLIER);
        int atrPeriod = getSettings().getInteger(ST_PERIOD);
        if (index < atrPeriod) return;
        Double atr = series.atr(index, atrPeriod);
        if (atr == null) return;

        float mid = (series.getHigh(index) + series.getLow(index))/2;
        double up = mid - (mult*atr);
        double down = mid + (mult*atr);

        Double pUp = series.getDouble(index-1, Values.UP);
        Double pDown = series.getDouble(index-1, Values.DOWN);
        Double pTrend = series.getDouble(index-1, Values.TREND);
        float pClose = series.getClose(index-1);

        if (pUp != null && pClose > pUp) up = Util.max(up, pUp);
        if (pDown != null && pClose < pDown) down = Util.min(down, pDown);

        double trend = 1;
        if (pDown != null && close > pDown) trend = 1;
        else if (pUp != null && close < pUp) trend = -1;
        else if (pTrend != null) trend = pTrend;

        series.setDouble(index, Values.TREND, trend);
        series.setDouble(index, Values.UP, up);
        series.setDouble(index, Values.DOWN, down);
        boolean trendChange = pTrend != null && trend != pTrend;
        //boolean completeBar = index < series.size()-1;
        boolean completeBar = true;//series.isBarComplete(index);

        boolean endOfBarExitOnOppositeSuperTrend=getSettings().getBoolean(EOB_EXIT_ON_SUPERTREND_OPPOSITE_TREND);
        boolean intraBarExitOnOppositeSuperTrend=getSettings().getBoolean(INTRABAR_EXIT_ON_SUPERTREND_OPPOSITE_TREND);
        if (trend > 0) {
            SuperTrendDirection=1;
            series.setDouble(index, Values.TSL, up);
            series.setPathColor(index, Values.TSL,  getSettings().getColor(Inputs.UP_COLOR, ctx.getDefaults().getGreenLine()));
            if (trendChange) {
                // Hack: when the direction changes make the path from the previous point to the new point "green"
                series.setPathColor(index-1, Values.TSL,  getSettings().getColor(Inputs.UP_COLOR, ctx.getDefaults().getGreenLine()));
                if (completeBar) {
                    MarkerInfo marker = getSettings().getMarker(Inputs.UP_MARKER);
                    String msg = get("SuperTrend Buy", close);
                    Marker m = new Marker(new Coordinate(series.getStartTime(index), series.getLow(index)), Enums.Position.BOTTOM, marker, msg);
                    // If there's already a cached drawn marker for this index, remove it first to prevent redraws.
                    if (intrabarMarkerCache!=null && intrabarMarkerIndCache==index) removeFigure(intrabarMarkerCache);
                    if (marker.isEnabled()) addFigure(m);
                    // If the bar's not complete, cache this intrabar-drawn marker's object and index to remove it and prevent redraws.
                    intrabarMarkerCache=m;
                    intrabarMarkerIndCache=index;
                    if (endOfBarExitOnOppositeSuperTrend && series.isBarComplete(index)) {
                        markBarCloseForST_Sell=false;
                        markBarCloseForST_Buy=false;
                        ctx.signal(index, Signals.ST_BUY, msg, close);
                    }
                   /* else if (endOfBarExitOnOppositeSuperTrend && !series.isBarComplete(index)) {
                        markBarCloseForST_Sell=false;
                        markBarCloseForST_Buy=true;
                    }*/
                    else if (intraBarExitOnOppositeSuperTrend && !endOfBarExitOnOppositeSuperTrend) {
                        markBarCloseForST_Sell=false;
                        markBarCloseForST_Buy=false;
                        ctx.signal(index, Signals.ST_BUY, msg, close);
                    }
                }
            }
        }
        else {
            SuperTrendDirection=-1;
            series.setDouble(index, Values.TSL, down);
            series.setPathColor(index, Values.TSL,  getSettings().getColor(Inputs.DOWN_COLOR, ctx.getDefaults().getRedLine()));
            if (trendChange) {
                // Hack: when the direction changes make the path from the previous point to the new point "red"
                series.setPathColor(index-1, Values.TSL,  getSettings().getColor(Inputs.DOWN_COLOR, ctx.getDefaults().getRedLine()));
                if (completeBar) {
                    MarkerInfo marker = getSettings().getMarker(Inputs.DOWN_MARKER);
                    String msg = get("SuperTrend Sell", close);
                    Marker m = new Marker(new Coordinate(series.getStartTime(index), series.getHigh(index)), Enums.Position.TOP, marker, msg);
                    // If there's already a cached drawn marker for this index, remove it first to prevent redraws.
                    if (intrabarMarkerCache!=null && intrabarMarkerIndCache==index) removeFigure(intrabarMarkerCache);
                    if (marker.isEnabled()) addFigure(m);
                    // If the bar's not complete, cache this intrabar-drawn marker's object and index to remove it and prevent redraws.
                    intrabarMarkerCache=m;
                    intrabarMarkerIndCache=index;

                    if (endOfBarExitOnOppositeSuperTrend && series.isBarComplete(index)) {
                        markBarCloseForST_Sell=false;
                        markBarCloseForST_Buy=false;
                        ctx.signal(index, Signals.ST_SELL, msg, close);
                    }
                    /*else if (endOfBarExitOnOppositeSuperTrend && !series.isBarComplete(index)){
                        markBarCloseForST_Sell=true;
                        markBarCloseForST_Buy=false;
                    }*/
                    else if (intraBarExitOnOppositeSuperTrend && !endOfBarExitOnOppositeSuperTrend) {
                        markBarCloseForST_Sell=false;
                        markBarCloseForST_Buy=false;
                        ctx.signal(index, Signals.ST_SELL, msg, close);
                    }
                }
            }
        }
        series.setComplete(index, series.isBarComplete(index));
    }

    private boolean markBarCloseForST_Sell=false;
    private boolean markBarCloseForST_Buy=false;

    private Marker intrabarMarkerCache=null;
    private int intrabarMarkerIndCache=-1;

    private void resetInternals(OrderContext ctx) {
        oCtx = ctx;

        entriesPlaced=0;
        placedBuyEntry=false;
        placedSellEntry=false;
        originalSignalPrice=Float.NaN;
        openPositions = 0;
        //currentTime=0L;
        //currentDay=0L;
    }

    @Override
    public void onSettingsUpdated(DataContext dataContext) {
        super.onSettingsUpdated(dataContext);
    }

    private boolean limitOrders = false;
    private String entryTime = "End of Bar";
    private int stopTicks = 50, profitTicks = 20, entryTicks = 4;


    // Add By Nia 10/11/2024
    private int additionalOrderTicks1 = 0;
    private int additionalOrderTicks2 = 0;
    private int additionalOrderTicks3 = 0;
    private int additionalOrderTicks4 = 0;
    private int additionalOrderTicks5 = 0;
    private int additionalOrderTicks6 = 0;
    private int additionalOrderTicks7 = 0;
    private int additionalOrderTicks8 = 0;
    private int additionalOrderTicks9 = 0;
    private int additionalOrderTicks10 = 0;

    private int additionalOrder1 = 0;
    private int additionalOrder2 = 0;
    private int additionalOrder3 = 0;
    private int additionalOrder4 = 0;
    private int additionalOrder5 = 0;
    private int additionalOrder6 = 0;
    private int additionalOrder7 = 0;
    private int additionalOrder8 = 0;
    private int additionalOrder9 = 0;
    private int additionalOrder10 = 0;

    private boolean placedBuyEntry=false,placedSellEntry=false, useTarget = true, useStopLoss=true;
    private int entriesPlaced = 0;
    private Double dayPnL = 0d;
    private int openPositions = 0;
    private int maxOpenPositions=20;
    private Double maxProfit=2000d,maxLoss=-2000d;
    private Float originalSignalPrice = Float.NaN;
    private Long currentDay = 0L;

    private void loadSettings() {
        Settings s = getSettings();
        limitOrders=s.getString(ORDER_TYPE).equals("Limit");
        stopTicks=s.getInteger(STOP_LOSS_TICKS);
        profitTicks = s.getInteger(TARGET_TICKS);
        useTarget=s.getBoolean(USE_TARGET, false);
        useStopLoss=s.getBoolean(USE_STOP_LOSS, false);

        // Add By Nia 10/11/2024
        // additionalOrderTicks=s.getInteger(ADDITIONAL_ORDER_TICKS);
        additionalOrderTicks1=s.getInteger(ADDITIONAL_ORDER_TICKS_1);
        additionalOrderTicks2=s.getInteger(ADDITIONAL_ORDER_TICKS_2);
        additionalOrderTicks3=s.getInteger(ADDITIONAL_ORDER_TICKS_3);
        additionalOrderTicks4=s.getInteger(ADDITIONAL_ORDER_TICKS_4);
        additionalOrderTicks5=s.getInteger(ADDITIONAL_ORDER_TICKS_5);
        additionalOrderTicks6=s.getInteger(ADDITIONAL_ORDER_TICKS_6);
        additionalOrderTicks7=s.getInteger(ADDITIONAL_ORDER_TICKS_7);
        additionalOrderTicks8=s.getInteger(ADDITIONAL_ORDER_TICKS_8);
        additionalOrderTicks9=s.getInteger(ADDITIONAL_ORDER_TICKS_9);
        additionalOrderTicks10=s.getInteger(ADDITIONAL_ORDER_TICKS_10);

        additionalOrder1 = s.getInteger(ADDITIONAL_ORDER_1);
        additionalOrder2 = s.getInteger(ADDITIONAL_ORDER_2);
        additionalOrder3 = s.getInteger(ADDITIONAL_ORDER_3);
        additionalOrder4 = s.getInteger(ADDITIONAL_ORDER_4);
        additionalOrder5 = s.getInteger(ADDITIONAL_ORDER_5);
        additionalOrder6 = s.getInteger(ADDITIONAL_ORDER_6);
        additionalOrder7 = s.getInteger(ADDITIONAL_ORDER_7);
        additionalOrder8 = s.getInteger(ADDITIONAL_ORDER_8);
        additionalOrder9 = s.getInteger(ADDITIONAL_ORDER_9);
        additionalOrder10 = s.getInteger(ADDITIONAL_ORDER_10);


        entryTicks=s.getInteger(ENTRY_TICKS);
        maxOpenPositions=s.getInteger(MAX_OPEN_POSITION);
        maxProfit=s.getDouble(PROFIT_TARGET_DOLLARS);
        maxLoss=s.getDouble(STOP_LOSS_DOLLARS);
        entryTime=s.getString(ENTRY_TIME);
    }

    @Override
    public void onSessionEnded(OrderContext ctx, TimeFrame session) {
        oCtx = ctx;

        // dayPnL = 0D;
        super.onSessionEnded(ctx,session);

    }

    @Override
    public void onSessionStarted(OrderContext ctx, TimeFrame session) {
        oCtx = ctx;

        // dayPnL = 0D;
        super.onSessionStarted(ctx,session);
    }

    private Long currentTime = 0L;
    @Override
    public void onBarClose(OrderContext ctx){
        if (markBarCloseForST_Sell&&ctx.getPosition()>0) {
            markBarCloseForST_Sell=false;
            ctx.closeAtMarket();
        }
        if (markBarCloseForST_Buy&&ctx.getPosition()<0) {
            markBarCloseForST_Buy=false;
            ctx.closeAtMarket();
        }

        oCtx = ctx;

        loadSettings();

        long mn = Util.getStartOfBar(ctx.getCurrentTime(), ctx.getCurrentTime(), ctx.getInstrument(),
                getSettings().getBarSize(BARSIZE), ctx.getDataContext().isRTH()) + 6 * Util.MILLIS_IN_HOUR;
   
        currentTime = ctx.getCurrentTime();
        if (mn != currentDay) { // New day.
            currentDay = mn;
            dayPnL=0D;
        }
        if (dayPnL + ctx.getUnrealizedPnL() >= maxProfit) ctx.closeAtMarket();
        if (dayPnL + ctx.getUnrealizedPnL() <= maxLoss) ctx.closeAtMarket();
        super.onBarClose(ctx);
    }

    @Override
    public void onBarUpdate(OrderContext ctx) {
        oCtx = ctx;

        loadSettings();
        long mn = Util.getStartOfBar(ctx.getCurrentTime(), ctx.getCurrentTime(), ctx.getInstrument(),
                getSettings().getBarSize(BARSIZE), ctx.getDataContext().isRTH()) + 6 * Util.MILLIS_IN_HOUR;


        currentTime = ctx.getCurrentTime();
        if (mn != currentDay) { // New day.
            currentDay = mn;

            dayPnL=0D;
        }
        // If pre-set daily loss or profit limit is breached, exit all.
        if (dayPnL + ctx.getUnrealizedPnL() >= maxProfit){
            ctx.closeAtMarket();
        }
        if (dayPnL + ctx.getUnrealizedPnL() <= maxLoss) ctx.closeAtMarket();
        super.onBarUpdate(ctx);
    }

    @Override
    public void onSignal(OrderContext ctx, Object signal) {

        // SuperTrend Options
        if (signal == Signals.ST_BUY) {
            if (ctx.getPosition()<0)ctx.closeAtMarket();
        } else if (signal == Signals.ST_SELL) {
            if (ctx.getPosition()>0)ctx.closeAtMarket();
        }

        // If we have an open position, ignore signals.
        if (ctx.getPosition() != 0 || !ctx.getActiveOrders().isEmpty() || dayPnL >= maxProfit || dayPnL <= maxLoss) return;
        Instrument ins = ctx.getInstrument();
        // If we get a buy signal
        int lotSize = getSettings().getTradeLots();
        boolean superTrendLongAllowed=!getSettings().getBoolean(REQUIRE_SUPERTREND_CONSISTENCY)||(getSettings().getBoolean(REQUIRE_SUPERTREND_CONSISTENCY)&&SuperTrendDirection==1);
        boolean superTrendShortAllowed=!getSettings().getBoolean(REQUIRE_SUPERTREND_CONSISTENCY)||(getSettings().getBoolean(REQUIRE_SUPERTREND_CONSISTENCY)&&SuperTrendDirection==-1);

        if (signal == Signals.BUY && superTrendLongAllowed) {
            placedBuyEntry=true;
            originalSignalPrice = ins.getLastPrice();
            if (limitOrders) { // Ask has to be greater than the desired entry price, else we need to use a market order
                Float desiredEntryPrice = (float) ins.round(ins.getLastPrice() - entryTicks * ins.getTickSize());

                if (ins.getAskPrice() > desiredEntryPrice) { // Can use limit order
                    ctx.submitOrders(ctx.createLimitOrder(Enums.OrderAction.BUY, Enums.TIF.GTC,lotSize /** 6*/,desiredEntryPrice));
                } else { // Else need to enter by market
                    ctx.buy(lotSize );//* 6);
                }

            } else ctx.buy(lotSize );//* 6);
            placeRegularlySpacedOrders(ctx, Enums.OrderAction.BUY, 10, 0);

        } else if (signal == Signals.SELL && superTrendShortAllowed) {
            placedSellEntry=true;
            originalSignalPrice = ins.getLastPrice();
            if (limitOrders) { // Bid has to be less than the desired entry price, else we need to use a market order
                Float desiredEntryPrice = (float) ins.round(ins.getLastPrice() + entryTicks * ins.getTickSize());
                if (ins.getBidPrice() < desiredEntryPrice) { // Can use limit order
                    ctx.submitOrders(ctx.createLimitOrder(Enums.OrderAction.SELL, Enums.TIF.GTC,lotSize/* * 6*/,desiredEntryPrice));
                } else { // Else need to enter by market
                    ctx.sell(lotSize );//* 6);
                }
            } else ctx.sell(lotSize );//* 6);
            placeRegularlySpacedOrders(ctx, Enums.OrderAction.SELL, 10, 0);
        }
        return;
    }

    private void placeRegularlySpacedOrders(OrderContext ctx, Enums.OrderAction action, int numOrders, int spacingOffset) {
        Instrument ins = ctx.getInstrument();
        int lotSize = getSettings().getTradeLots();
        int entriesAlreadyPlaced = entriesPlaced;
        if (action.equals(Enums.OrderAction.BUY)) {
            for (int i = spacingOffset + 1; i <= Math.min(maxOpenPositions-1, entriesAlreadyPlaced + numOrders); i ++) {
                entriesPlaced++;
                Float desiredEntryPrice = (float) ins.round(originalSignalPrice - additionalOrderTicks * i * ins.getTickSize());
                if (ins.getAskPrice() > desiredEntryPrice) { // Can use limit order
                    ctx.submitOrders(ctx.createLimitOrder(Enums.OrderAction.BUY, Enums.TIF.GTC,lotSize /** 6*/,desiredEntryPrice));
                } else { // Else need to enter by market
                    ctx.buy(lotSize );//* 6);
                }
            }
        } else {
            for (int i = spacingOffset + 1; i <= Math.min(maxOpenPositions-1, entriesAlreadyPlaced + numOrders); i ++) {
                entriesPlaced++;
                Float desiredEntryPrice = (float) ins.round(originalSignalPrice + additionalOrderTicks * i * ins.getTickSize());
                if (ins.getBidPrice() < desiredEntryPrice) { // Can use limit order
                    ctx.submitOrders(ctx.createLimitOrder(Enums.OrderAction.SELL, Enums.TIF.GTC,lotSize /** 6*/,desiredEntryPrice));
                } else { // Else need to enter by market
                    ctx.sell(lotSize );//* 6);
                }
            }
        }
    }

    @Override
    public void onOrderCancelled(OrderContext ctx, Order order) {
        resetInternals(ctx);
        super.onOrderCancelled(ctx,order);
    }

    private void placeStopLoss(OrderContext ctx, Enums.OrderAction action, Float fillPrice) {
        Instrument ins = ctx.getInstrument();
        if (action.equals(Enums.OrderAction.SELL)) {
            Float desiredExitPrice = (float) ins.round(fillPrice + stopTicks * ins.getTickSize());
            if (ins.getAskPrice() < desiredExitPrice) { // Can use limit order
                ctx.submitOrders(ctx.createStopOrder(Enums.OrderAction.BUY, Enums.TIF.GTC,ctx.getPosition(),desiredExitPrice));
            } else { // Else need to enter by market
                ctx.sell(ctx.getPosition());
            }
        }
        else {
            Float desiredExitPrice = (float) ins.round(fillPrice - stopTicks * ins.getTickSize());
            if (ins.getBidPrice() > desiredExitPrice) { // Can use limit order
                ctx.submitOrders(ctx.createStopOrder(Enums.OrderAction.SELL, Enums.TIF.GTC,ctx.getPosition(),desiredExitPrice));
            } else { // Else need to enter by market
                ctx.buy(ctx.getPosition());
            }
        }
    }

    private void adjustStopLoss(OrderContext ctx, Enums.OrderAction action) {
        if (ctx.getPosition() == 0) return;
        Order PT = null;
        if (!ctx.getActiveOrders().isEmpty())
            for (Order o : ctx.getActiveOrders()) {
                if (o.getType().equals(Enums.OrderType.STOP) &&
                        (action.equals(Enums.OrderAction.SELL) ?
                                o.getAdjStopPrice() <= ctx.getAvgEntryPrice() :
                                o.getAdjStopPrice() >= ctx.getAvgEntryPrice()
                        )
                )
                    PT = o;
            }

        if (PT!=null) {
            Instrument ins = ctx.getInstrument();
            Float avgEntry = ins.round(ctx.getAvgEntryPrice());
            if (action.equals(Enums.OrderAction.SELL)) {
                Float desiredEntryPrice = (float) ins.round(avgEntry - stopTicks * ins.getTickSize());
                if (ins.getBidPrice() > desiredEntryPrice) { // Can use limit order
                    PT.setAdjStopPrice(desiredEntryPrice);
                    PT.setAdjQuantity(ctx.getPosition());
                    ctx.submitOrders(PT);
                } else { // Else need to enter by market
                    ctx.cancelOrders(PT);
                    ctx.sell(ctx.getPosition());
                }
            }
            else {
                Float desiredEntryPrice = (float) ins.round(avgEntry + stopTicks * ins.getTickSize());

                if (ins.getAskPrice() < desiredEntryPrice) { // Can use limit order
                    PT.setAdjStopPrice(desiredEntryPrice);
                    PT.setAdjQuantity(ctx.getPosition());
                    ctx.submitOrders(PT);
                } else { // Else need to enter by market
                    ctx.cancelOrders(PT);
                    ctx.buy(ctx.getPosition());
                }
            }
        }
    }

    private void placeProfitTarget(OrderContext ctx, Enums.OrderAction action, Float fillPrice) {
        Instrument ins = ctx.getInstrument();
        if (action.equals(Enums.OrderAction.SELL)) {
            Float desiredExitPrice = (float) ins.round(fillPrice + profitTicks * ins.getTickSize());
            if (ins.getBidPrice() < desiredExitPrice) { // Can use limit order
                ctx.submitOrders(ctx.createLimitOrder(Enums.OrderAction.SELL, Enums.TIF.GTC,ctx.getPosition(),desiredExitPrice));
            } else { // Else need to enter by market
                ctx.sell(ctx.getPosition());
            }
        }
        else {
            Float desiredExitPrice = (float) ins.round(fillPrice - profitTicks * ins.getTickSize());
            if (ins.getAskPrice() > desiredExitPrice) { // Can use limit order
                ctx.submitOrders(ctx.createLimitOrder(Enums.OrderAction.BUY, Enums.TIF.GTC,ctx.getPosition(),desiredExitPrice));
            } else { // Else need to enter by market
                ctx.buy(ctx.getPosition());
            }
        }
    }

    private void adjustProfitTarget(OrderContext ctx, Enums.OrderAction action) {
        if (ctx.getPosition() == 0) return;
        Order PT = null;

        if (!ctx.getActiveOrders().isEmpty())
            for (Order o : ctx.getActiveOrders()) {
                if (o.getType().equals(Enums.OrderType.LIMIT) &&
                        (action.equals(Enums.OrderAction.SELL) ?
                                o.getAdjLimitPrice() >= ctx.getAvgEntryPrice() :
                                o.getAdjLimitPrice() <= ctx.getAvgEntryPrice()
                        )
                )
                    PT = o;
            }

        if (PT!=null) {
            Instrument ins = ctx.getInstrument();
            Float avgEntry = ins.round(ctx.getAvgEntryPrice());
            if (action.equals(Enums.OrderAction.SELL)) {
                Float desiredEntryPrice = (float) ins.round(avgEntry + profitTicks * ins.getTickSize());
                if (ins.getBidPrice() < desiredEntryPrice) { // Can use limit order
                    PT.setAdjLimitPrice(desiredEntryPrice);
                    PT.setAdjQuantity(ctx.getPosition());
                    ctx.submitOrders(PT);
                } else { // Else need to enter by market
                    ctx.cancelOrders(PT);
                    ctx.sell(ctx.getPosition());
                }
            }
            else {
                Float desiredEntryPrice = (float) ins.round(avgEntry - profitTicks * ins.getTickSize());

                if (ins.getAskPrice() > desiredEntryPrice) { // Can use limit order
                    PT.setAdjLimitPrice(desiredEntryPrice);
                    PT.setAdjQuantity(ctx.getPosition());
                    ctx.submitOrders(PT);
                } else { // Else need to enter by market
                    ctx.cancelOrders(PT);
                    ctx.buy(ctx.getPosition());
                }
            }
        }
    }



    @Override
    public void onOrderFilled(OrderContext ctx, Order order) {

        // If we are already long and a profit target is in place, adjust its size and position
        if (ctx.getPosition() > 0 && order.getAction().equals(Enums.OrderAction.BUY)) openPositions ++;

        if (ctx.getPosition()!=0) {
            if (useStopLoss) adjustStopLoss(ctx, ctx.getPosition() > 0 ? Enums.OrderAction.SELL : Enums.OrderAction.BUY);
            if (useTarget) adjustProfitTarget(ctx, ctx.getPosition() > 0 ? Enums.OrderAction.SELL : Enums.OrderAction.BUY);
        }
        // If it is an initial buy entry
        if (placedBuyEntry){
            openPositions = 1;
            placedBuyEntry=false;
            if (useStopLoss) placeStopLoss(ctx, Enums.OrderAction.BUY, order.getAvgFillPrice());
            if (useTarget) placeProfitTarget(ctx, Enums.OrderAction.SELL, order.getAvgFillPrice());
            return;
        }
        if (placedSellEntry) {
            openPositions = 1;
            placedSellEntry=false;
            if (useStopLoss) placeStopLoss(ctx, Enums.OrderAction.SELL, order.getAvgFillPrice());
            if (useTarget) placeProfitTarget(ctx, Enums.OrderAction.BUY, order.getAvgFillPrice());
            return;
        }

        // If it is an additional buy entry
        if (ctx.getPosition() > getSettings().getTradeLots()/**6 */&& order.getAction() == Enums.OrderAction.BUY) {
            // Place another one lower down the chain
            if (openPositions < maxOpenPositions && !(dayPnL >= maxProfit || dayPnL <= maxLoss))
                placeRegularlySpacedOrders(ctx, Enums.OrderAction.BUY,1, entriesPlaced);
        }
        // else if it is an additional sell entry
        else if (ctx.getPosition() < -getSettings().getTradeLots()/**6*/ && order.getAction() == Enums.OrderAction.SELL) {
            // Place another one high up the chain
            if (openPositions < maxOpenPositions && !(dayPnL >= maxProfit || dayPnL <= maxLoss))
                placeRegularlySpacedOrders(ctx, Enums.OrderAction.SELL,1, entriesPlaced);
        }
    }




    // 1.
    /*


Every entry should be for 6 contracts (or 6 minimum entry lots, that could be adjusted). ( 1 contract based on the signal + 1 PROFIT order +
 5 LIMIT orders placed above/below entry-level at the pre-set levels (Short Entry at the market at 100, STOP at 96, LIMIT at 102, 104, 106, 108 and 110)

    If the PROFIT order is reached, EA cancels all LIMIT orders and enters the NEW order only after the NEW signal is generated.
If LIMIT order is reached, the EA should ADD another LIMIT order (112 etc.), cancel 1 PROFIT order and place 2 (etc) NEW PROFIT orders
set at a preset level above/below NEW average entry price for 2 orders. This is a very important part, in the fast-moving market
I need to always have 5 LIMIT orders and the correct number of orders set as a PROFIT.
The EA should ignore all new signals until









Trading signals generated by SRO indicator. EA setting should have options for changing the default indicator setting.
Once the signal is generated, the EA should enter the Buy / Sell order. EA should be set to enter either Market or LIMIT / STOP order 1-2-3 etc. tick above/below the last market price when the signal was generated.
The EA should enter the PROFT order immediately after the order is filled. The EA should have a setting where PROFIT order should be placed ( 1-2-3 etc. ticks above/below entry)
 When placing an order upon signal generation, the EA should also place 4 other orders at the same time, that will be above/below the entry-level. For example, if the order is placed
 at 100 and filled at 99, the EA should also place four other orders at 103-107-111-115, if the initial setting was set for 4 tics.
When the order entered by EA, with the setting of 10 ticks PROFIT and 4 ticks additional orders, there should be the following positions, for example, SHORT open position at 100, a BUY order at 90,
and four other LIMIT orders at SELL 104, 108, 112, 116.
If the PROFIT is reached, the EA should cancel all placed orders.
If the first target for LIMIT order is reached and filled, the EA should cancel BUY at 90 and enter 2 BUY orders at 92 ( average price for two orders will be 100+104/2=102 - 10 ticks profit=92).
Once 1st target of LIMIT orders is reached, the EA should place another order to SELL 4 tick above the last unfilled order, in this example, it would be 120. At this point, there should be 2 SHORT
open positions with an average price of 102, 2 BUY profit orders at 92, and 4 LIMIT orders at 108, 112, 116, 120.
This process should continue until one of 3 options are reached: a) The positions are closed at the profit b) the positions are closed manually c) the positions are closed when the pre-set daily loss limit is reached.
Once we have open positions the EA should ignore new signals generated by SRO indicator.
Once open positions are closed and there are no other open positions, the new trade should be placed when a new signal is generated.
The EA should have a setting to choose trading hours when EA is working.
The EA should stop trading when the pre-set amount of PROFIT or LOSS is reached for the day. It could be based on the dollar amount or on the percent of equity on the trading account.
The EA should have a setting for default order quantity. If it set at 1, then the first order placed will 1, if it set at 3, then the first order placed will be 3 lots and all additional orders above/below
 will be placed at 3 lots as well.
     */
}
