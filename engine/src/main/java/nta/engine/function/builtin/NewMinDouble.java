package nta.engine.function.builtin;

import nta.catalog.Column;
import nta.catalog.proto.CatalogProtos;
import nta.datum.Datum;
import nta.datum.DatumFactory;
import nta.engine.function.AggFunction;
import nta.engine.function.FunctionContext;
import nta.storage.Tuple;
import nta.storage.VTuple;

/**
 * @author Hyunsik Choi
 */
public class NewMinDouble extends AggFunction<Datum> {

  public NewMinDouble() {
    super(new Column[] {
        new Column("val", CatalogProtos.DataType.DOUBLE)
    });
  }

  @Override
  public FunctionContext newContext() {
    return new MinContext();
  }

  @Override
  public void eval(FunctionContext ctx, Tuple params) {
    MinContext minCtx = (MinContext) ctx;
    minCtx.min = Math.min(minCtx.min, params.get(0).asDouble());
  }

  @Override
  public Tuple getPartialResult(FunctionContext ctx) {
    Tuple part = new VTuple(1);
    part.put(0, DatumFactory.createDouble(((MinContext)ctx).min));
    return part;
  }

  @Override
  public Datum terminate(FunctionContext ctx) {
    return DatumFactory.createDouble(((MinContext)ctx).min);
  }

  private class MinContext implements FunctionContext {
    double min = Double.MAX_VALUE;
  }
}