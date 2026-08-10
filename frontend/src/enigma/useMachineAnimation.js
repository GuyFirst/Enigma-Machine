import { useCallback, useEffect, useRef, useState } from 'react'

// Playback speeds up as it goes: the first letters are slow enough to follow,
// then it settles into a rhythm so long messages don't drag.
const FIRST_DELAY_MS = 260
const MIN_DELAY_MS = 45
const DECAY = 0.9

function delayFor(index) {
  return Math.max(MIN_DELAY_MS, FIRST_DELAY_MS * DECAY ** index)
}

const IDLE = {
  status: 'idle',
  index: -1,
  step: null,
  positions: null,
  input: '',
  output: '',
}

/**
 * Plays a machine run (the `steps` from EnigmaMachine.process) as an animation,
 * exposing the state the machine visual renders from. Nothing is computed here -
 * the cipher already ran; this walks its recorded steps.
 */
export function useMachineAnimation() {
  const [state, setState] = useState(IDLE)
  const timerRef = useRef(null)
  const runRef = useRef(null)

  const clearTimer = () => {
    if (timerRef.current) {
      clearTimeout(timerRef.current)
      timerRef.current = null
    }
  }

  useEffect(() => clearTimer, [])

  const finish = useCallback(() => {
    const run = runRef.current
    if (!run) return
    clearTimer()
    const last = run.steps[run.steps.length - 1]
    setState({
      status: 'done',
      index: run.steps.length - 1,
      step: null,
      positions: last ? last.positionsAfter : run.startPositions,
      input: run.steps.map((s) => s.inputChar).join(''),
      output: run.steps.map((s) => s.outputChar).join(''),
    })
    run.onDone?.()
  }, [])

  const advance = useCallback(
    (index) => {
      const run = runRef.current
      if (!run) return
      if (index >= run.steps.length) {
        finish()
        return
      }

      const step = run.steps[index]
      setState({
        status: 'running',
        index,
        step,
        positions: step.positionsAfter,
        input: run.steps.slice(0, index + 1).map((s) => s.inputChar).join(''),
        output: run.steps.slice(0, index + 1).map((s) => s.outputChar).join(''),
      })

      timerRef.current = setTimeout(() => advance(index + 1), delayFor(index))
    },
    [finish],
  )

  /** @param {{steps:Array, startPositions:string[], onDone?:Function}} run */
  const play = useCallback(
    (run) => {
      clearTimer()
      runRef.current = run
      if (!run.steps.length) {
        setState({ ...IDLE, status: 'done', positions: run.startPositions })
        run.onDone?.()
        return
      }
      setState({
        status: 'running',
        index: -1,
        step: null,
        positions: run.startPositions,
        input: '',
        output: '',
      })
      timerRef.current = setTimeout(() => advance(0), FIRST_DELAY_MS)
    },
    [advance],
  )

  const skip = useCallback(() => finish(), [finish])

  const reset = useCallback(() => {
    clearTimer()
    runRef.current = null
    setState(IDLE)
  }, [])

  return { ...state, play, skip, reset, isRunning: state.status === 'running' }
}
