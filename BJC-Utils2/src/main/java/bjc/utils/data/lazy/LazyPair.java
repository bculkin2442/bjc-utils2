package bjc.utils.data.lazy;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import bjc.utils.data.IPair;

public class LazyPair<L, R> implements IPair<L, R> {
	protected LazyHolder<IPair<L, R>> del;

	@Override
	public <L2, R2> IPair<L2, R2> apply(Function<L, L2> lf,
			Function<R, R2> rf) {
		return del.unwrap((par) -> par.apply(lf, rf));
	}

	@Override
	public <E> E merge(BiFunction<L, R, E> bf) {
		return del.unwrap((par) -> par.merge(bf));
	}

	@Override
	public void doWith(BiConsumer<L, R> bc) {
		del.doWith((par) -> {
			par.doWith(bc);
		});
	}

}
