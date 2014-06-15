package com.truward.polymer.code.visitor.parent;

import com.truward.polymer.code.Jst;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent manager, provides a combination of parent provider for a code that accesses parent information
 * and parent sink for parent aware visitor, that records parent information.
 *
 * @author Alexander Shabanov
 */
public final class ParentManager implements ParentProvider, ParentSink {
  private final List<Jst.Node> nodes = new ArrayList<>(20);

  // why 2? - +1 is an offset of the last inserted element (this element),
  // and +1 is an offset of element before the last inserted one, i.e. parent of current node.
  private static final int OFFSET_OF_PARENT_NODE = 2;

  @Nonnull
  @Override
  public Jst.Node getParent() {
    final int last = nodes.size() - OFFSET_OF_PARENT_NODE;
    if (last < 0) {
      throw new IllegalStateException("No parent node recorded");
    }
    return nodes.get(last);
  }

  @Override
  public void push(@Nonnull Jst.Node node) {
    nodes.add(node);
  }

  @Override
  public void pop() {
    if (nodes.isEmpty()) {
      throw new IllegalStateException("No parent node recorded");
    }
    nodes.remove(nodes.size() - 1);
  }
}
